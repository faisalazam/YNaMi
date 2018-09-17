package penetration.pk.lucidxpo.ynami.steps.domain;

import lombok.Getter;
import lombok.Setter;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@Setter
public class ZAPFalsePositive {
    private String url;
    private String parameter;
    private Integer cweId;
    private Integer wascId;

    public ZAPFalsePositive(final String url,
                            final String parameter,
                            final String cweId,
                            final String wascId) {
        this.url = url;
        this.parameter = parameter;
        if (isBlank(cweId)) {
            this.cweId = null;
        } else {
            try {
                this.cweId = parseInt(cweId);
            } catch (final NumberFormatException ignored) {
            }
        }

        if (isBlank(wascId)) {
            this.wascId = null;
        } else {
            try {
                this.wascId = parseInt(cweId);
            } catch (final NumberFormatException ignored) {
            }
        }
    }

    public boolean matches(final String url, final String parameter, final int cweid, final int wascId) {
        if (isUrlMatched(url) && isParameterMatched(parameter)) {
            if (this.cweId != null && this.cweId == cweid) {
                return true;
            }
            return this.wascId != null && this.wascId == wascId;
        }
        return false;
    }

    private boolean isUrlMatched(final String url) {
        return this.url != null
                && url != null
                && url.matches(this.url);
    }

    private boolean isParameterMatched(final String parameter) {
        return this.parameter != null
                && parameter != null
                && parameter.matches(this.parameter);
    }
}