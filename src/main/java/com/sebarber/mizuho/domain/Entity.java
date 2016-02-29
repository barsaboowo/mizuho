package com.sebarber.mizuho.domain;

import java.io.Serializable;

/**
 * Created by B on 27/02/2016.
 */
public interface Entity extends Serializable {   

    public abstract boolean equals(Object other);
    public abstract int hashCode();

}
