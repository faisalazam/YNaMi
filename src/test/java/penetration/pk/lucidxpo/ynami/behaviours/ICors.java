package penetration.pk.lucidxpo.ynami.behaviours;

public interface ICors {
    void makeCorsRequest(String path, String origin);

    String getAccessControlAllowOriginHeader();
}