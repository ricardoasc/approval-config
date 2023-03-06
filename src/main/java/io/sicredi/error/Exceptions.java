package io.sicredi.error;
import io.sicredi.services.platform.error.BusinessException;
import io.sicredi.services.platform.error.NotFoundException;

public class Exceptions {
    public static BusinessException businessException(final ErrorDefinition errorDefinition) {
        return new BusinessException(errorDefinition.getMessage(), errorDefinition.getCode());
    }

    public static NotFoundException notFoundException(final ErrorDefinition errorDefinition) {
        return new NotFoundException(errorDefinition.getMessage(), errorDefinition.getCode());
    }
}
