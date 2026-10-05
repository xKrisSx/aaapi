package pl.notkris.aaapi.guice.fixture;

import com.google.inject.Inject;

@Registered
public class FirstComponent {

    public final SharedService service;
    public final String name;

    @Inject
    public FirstComponent(SharedService service, String name) {
        this.service = service;
        this.name = name;
    }
}
