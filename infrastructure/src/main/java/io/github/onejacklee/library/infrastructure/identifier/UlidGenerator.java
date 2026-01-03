package io.github.onejacklee.library.infrastructure.identifier;

import com.github.f4b6a3.ulid.UlidCreator;
import io.github.onejacklee.library.common.application.IdGenerator;
import org.springframework.stereotype.Component;

@Component
public class UlidGenerator implements IdGenerator {

    @Override
    public String generate() {
        return UlidCreator.getUlid().toString();
    }
}
