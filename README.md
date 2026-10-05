# AAAPI — Annotation (based) Autoregister API

A lightweight, modular Java library for automatic class detection and registration based on annotations.
Instead of manually registering every class, you annotate it — AAAPI handles the rest.

---

## Requirements to build

- Java 21+
- Gradle with Kotlin DSL

---

## Installation
### Gradle
Add JitPack to your repositories:

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("com.github.xKrisSx.aaapi:core:1.1.1")

    // optional Guice support (brings Guice 7 transitively):
    implementation("com.github.xKrisSx.aaapi:guice:1.1.1")
}
```

### Maven
Add JitPack to your repositories:

```xml
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
```

Then add the dependency:

```xml
    <dependency>
        <groupId>com.github.xKrisSx.aaapi</groupId>
        <artifactId>core</artifactId>
        <version>1.1.1</version>
    </dependency>
    <!-- optional Guice support: -->
    <dependency>
        <groupId>com.github.xKrisSx.aaapi</groupId>
        <artifactId>guice</artifactId>
        <version>1.1.1</version>
    </dependency>
```

---

## Modules

| Module | Description |
|---|---|
| `core` | Main API |
| `guice` | Optional Google Guice DI integration |
| `examples/paper` | Paper Brigadier commands, listeners, tasks |
| `examples/cloud` | Cloud command framework integration |
| `examples/aikar` | Aikar's Command Framework integration |
| `examples/lite-commands` | LiteCommands integration |
| `examples/paper-guice` | Guice dependency injection example |

---

# Why AAAPI?
## The problem

```java
private void registerListeners() {
    getServer().getPluginManager().registerEvents(new ChatListener(), this);
    getServer().getPluginManager().registerEvents(new JoinListener(), this);
    getServer().getPluginManager().registerEvents(new DeathListener(), this);
    // ...20 more lines
}
```

## The solution

```java
@ListenerLoader.RegisteredListener
public class ChatListener implements Listener { ... }

@ListenerLoader.RegisteredListener  
public class JoinListener implements Listener { ... }
```

```java
registry.register(new ListenerLoader(this));
registry.loadAll(); // finds and registers everything automatically
```

---

## How it works

### 1. Create a loader

A loader defines its own annotation and handles the registration logic:

```java
public class ListenerLoader extends ClassTypeLoader {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredListener {}

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredListener.class;
    }
    
    private final MyPlugin plugin;
    public ListenerLoader(MyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        plugin.getServer().getPluginManager().registerEvents((Listener) instance, plugin);
    }
}
```

### 2. Annotate your classes

```java
@ListenerLoader.RegisteredListener
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome!");
    }
}
```

### 3. Wire it up

```java
LoaderRegistry registry = new LoaderRegistry(
    "com.yourpackage",              // <-- package to scan
    getClass().getClassLoader(),    // <-- classloader to use
    new DefaultInstanceProvider(),  // <-- default instance provider
    getLogger()                     // <-- optional, defaults to the "AAAPI" logger
);

// register all loaders
registry.register(new ListenerLoader(this));
registry.loadAll();
```

### Scanning rules

- Only classes inside the given package (and its subpackages) are scanned.
- A class must carry the annotation **directly**. Subclasses of an annotated class are not registered
  (unless the annotation is marked `@Inherited`).
- Abstract classes and interfaces are skipped.
- A class annotated for several loaders is instantiated **once** and the same instance is passed to every matching loader.
- A failure of one class (constructor or loader exception) is logged and doesn't stop the remaining classes.

> **Paper:** if you register commands inside `LifecycleEvents.COMMANDS`, use a separate registry for them.
> That event fires again on `/minecraft:reload`, so calling `loadAll()` for listeners there would register them twice.

---

## Custom annotations

You can define your own annotations and check for them inside any loader thanks to reflections:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PhysicListener {}
```

```java
@ListenerLoader.RegisteredListener
@PhysicListener
public class BlockFromToListener implements Listener { ... }
```

```java
@Override
public void process(Class<?> clazz, Object instance) {
    Bukkit.getPluginManager().registerEvents((Listener) instance, plugin);

    if (clazz.isAnnotationPresent(PhysicListener.class)) {
        boolean enabled = clazz.getAnnotation(PhysicListener.class).isEnabledByDefault();
        WorldUtils.addPhysicListener((Listener) instance, enabled);
    }
}
```

---

## Guice integration

Swap `DefaultInstanceProvider` for `GuiceInstanceProvider` to enable full dependency injection:

```java
Injector injector = Guice.createInjector(new PluginModule(this));

LoaderRegistry registry = new LoaderRegistry(
    "com.yourpackage",
    getClass().getClassLoader(),
    new GuiceInstanceProvider(injector), // <-- only change
    getLogger()
);
```

A module makes the plugin and server objects injectable:

```java
public class PluginModule extends AbstractModule {

    private final MyPlugin plugin;

    public PluginModule(MyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(MyPlugin.class).toInstance(plugin);
        bind(Plugin.class).toInstance(plugin);
        bind(Server.class).toInstance(plugin.getServer());
    }
}
```

Your classes can now use `@Inject` constructors:

```java
@ListenerLoader.RegisteredListener
public class JoinListener implements Listener {

    private final GreetingService greetingService;

    @Inject
    public JoinListener(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
}
```

```java
@Singleton // <-- one shared instance; without it every injection point gets a new one
public class GreetingService { ... }
```

Notes:
- Use `com.google.inject.Inject` or `jakarta.inject.Inject`. Guice 7 does **not** recognize the legacy `javax.inject.Inject`.
- Paper already ships Guava (a Guice dependency), so you can exclude it from your shadow jar:
  ```kotlin
  tasks.shadowJar {
      dependencies {
          exclude(dependency("com.google.guava:.*"))
      }
  }
  ```
