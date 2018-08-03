package pk.lucidxpo.ynami.service.sample;

public class NewToggleableServiceImpl implements ToggleableService {

    @Override
    public String getSomeValue() {
        return "Value from new service implementation";
    }
}