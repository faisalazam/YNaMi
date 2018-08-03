package pk.lucidxpo.ynami.service.sample;

public class OldToggleableServiceImpl implements ToggleableService {

    @Override
    public String getSomeValue() {
        return "Value from old service implementation";
    }
}