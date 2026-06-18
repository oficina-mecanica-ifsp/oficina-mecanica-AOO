package com.projetoweb.oficinamecanica.entities.enums;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {

    ABERTO {
        @Override public Set<OrderStatus> nextStates() { return EnumSet.of(EM_ANDAMENTO, EM_ATRASO); }
    },
    EM_ANDAMENTO {
        @Override public Set<OrderStatus> nextStates() { return EnumSet.of(FINALIZADO, EM_ATRASO); }
    },
    EM_ATRASO {
        @Override public Set<OrderStatus> nextStates() { return EnumSet.of(EM_ANDAMENTO); }
    },
    FINALIZADO {
        @Override public Set<OrderStatus> nextStates() { return EnumSet.noneOf(OrderStatus.class); }
    };

    public abstract Set<OrderStatus> nextStates();

    public boolean canTransitionTo(OrderStatus next) {
        return nextStates().contains(next);
    }
}
