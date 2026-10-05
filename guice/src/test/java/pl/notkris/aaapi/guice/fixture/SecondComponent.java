package pl.notkris.aaapi.guice.fixture;

import com.google.inject.Inject;

@Registered
public class SecondComponent {

    public final SharedService service;

    @Inject
    public SecondComponent(SharedService service) {
        this.service = service;
    }
}
