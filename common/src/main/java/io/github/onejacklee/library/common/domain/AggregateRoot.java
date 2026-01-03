package io.github.onejacklee.library.common.domain;

import java.util.Objects;

public abstract class AggregateRoot<TId> implements Entity<TId> {

    private TId id;

    protected AggregateRoot() {
        // For JPA
    }

    protected AggregateRoot(TId id) {
        this.id = Objects.requireNonNull(id, "Id cannot be null");
    }

    @Override
    public TId getId() {
        return id;
    }

    protected void setId(TId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot<?> that = (AggregateRoot<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
