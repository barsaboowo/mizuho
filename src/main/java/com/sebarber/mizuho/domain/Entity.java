package com.sebarber.mizuho.domain;

import java.io.Serializable;

/**
 * Created by B on 27/02/2016.
 */
public abstract class Entity implements Serializable {
    private static final long serialVersionUID = 6759290149875433151L;

    public abstract boolean equals(Object other);
    public abstract int hashCode();

}
