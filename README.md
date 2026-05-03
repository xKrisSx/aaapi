# AAAPI — Annotation (based) Autoregister API

A lightweight, modular Java library for automatic class detection and registration based on annotations.
Instead of manually registering every class, you annotate it — AAAPI handles the rest.

---

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

## Modules

| Module | Description |
|---|---|
| `core` | Main API |
| `guice` | Optional Google Guice DI integration |
| `examples/paper` | Paper Brigadier commands, listeners, tasks |
| `examples/cloud` | Cloud command framework integration |
| `examples/acf` | Aikar's Command Framework integration |
| `examples/lite-commands` | LiteCommands integration |
| `examples/guice-example` | Guice dependency injection example |

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
    new DefaultInstanceProvider()   // <-- default instance provider
);

// register all loaders
registry.register(new ListenerLoader(this));
registry.loadAll();
```

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
        WorldUtils.addPhysicListener((Listener) instance);
    }
}
```

---

## Guice integration

Swap `DefaultInstanceProvider` for `GuiceInstanceProvider` to enable full dependency injection:

```java
Injector injector = Guice.createInjector();

LoaderRegistry registry = new LoaderRegistry(
    "com.yourpackage",
    getClass().getClassLoader(),
    new GuiceInstanceProvider(injector) // <-- only change
);
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

---

## Requirements

- Java 21+
- Gradle with Kotlin DSL

---

## Installation

Add JitPack to your repositories:

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("com.github.xKrisSx:aaapi:core:1.0.0")

    // optional Guice support:
    implementation("com.github.xKrisSx:aaapi:guice:1.0.0")
}
```