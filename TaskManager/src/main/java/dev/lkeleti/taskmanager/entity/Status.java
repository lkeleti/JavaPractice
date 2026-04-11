package dev.lkeleti.taskmanager.entity;

import lombok.Getter;

import static java.lang.Integer.MAX_VALUE;

@Getter
public enum Status {
    TODO(0),
    IN_PROGRESS(1),
    DONE(2);

    private final int order;

    Status(int order) {
        this.order = order;
    }

    public static Status getFirst() {
        int defOrder = MAX_VALUE;
        Status defStatus = null;
        for (Status status : Status.values()) {
            if (status.getOrder() < defOrder) {
                defOrder = status.getOrder();
                defStatus = status;
            }
        }
        return defStatus;
    }
}