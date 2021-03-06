package io.github.guoxinl.protocol.analysis.conf.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by guoxin on 18-3-8.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProtocolSet<V> extends HashSet<V> {

    private static final long serialVersionUID = 4624985474330828728L;

    public static <V> ProtocolSet<V> newDataProtocolMap(){
        return new ProtocolSet<>();
    }

    public void register(V element) {
        add(element);
    }

    protected ProtocolSet(Collection<? extends V> c) {
        super(c);
    }

}
