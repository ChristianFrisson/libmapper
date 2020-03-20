
package mpr;

import mpr.graph.*;
//import mpr.map.*;
//import mpr.signal.*;
import java.util.*;
//import java.util.HashSet;

public class Graph
{
    /* constructor */
    private native long mprGraphNew(int flags);
    public Graph(Set<Type> types) {
        int flags = 0;
        for (Type t : Type.values()) {
            if (types.contains(t))
                flags |= t.value();
        }
        _graph = mprGraphNew(flags);
    }
    public Graph(Type type) {
        _graph = mprGraphNew(type.value());
        _listeners = new HashSet();
    }
    public Graph() {
        _graph = mprGraphNew(Type.OBJECT.value());
        _listeners = new HashSet();
    }
    public Graph(long graph) {
        _graph = graph;
        _listeners = new HashSet();
    }

    /* free */
    private native void mprGraphFree(long graph);
    public void free() {
        if (_graph != 0)
            mprGraphFree(_graph);
        _graph = 0;
    }

    /* interface */
    public native String getInterface();
    public native Graph setInterface(String iface);

    /* multicast address */
    public native String getAddress();
    public native Graph setAddress(String group, int port);

    /* poll */
    public native Graph poll(int timeout);
    public Graph poll() { return poll(0); }

    /* subscriptions */
    private native void mprGraphSubscribe(long graph, mpr.Device dev, int flags,
                                          int timeout);
    public Graph subscribe(mpr.Device dev, Type type, int lease) {
        mprGraphSubscribe(_graph, dev, type.value(), lease);
        return this;
    }
    public Graph subscribe(mpr.Device dev, Type type) {
        return subscribe(dev, type, -1);
    }
    public Graph subscribe(mpr.Device dev, Set<Type> types, int lease) {
        int flags = 0;
        for (Type t : Type.values()) {
            if (types.contains(t))
                flags |= t.value();
        }
        mprGraphSubscribe(_graph, dev, flags, lease);
        return this;
    }
    public Graph subscribe(mpr.Device dev, Set<Type> types) {
        return subscribe(dev, types, -1);
    }

    public native Graph unsubscribe(mpr.Device dev);

    // Listeners
    private native void addCallback(long graph, Listener l);
    public Graph addListener(Listener l) {
        addCallback(_graph, l);
        _listeners.add(l);
        return this;
    }

    private native void removeCallback(long graph, Listener l);
    public Graph removeListener(Listener l) {
        removeCallback(_graph, l);
        _listeners.remove(l);
        return this;
    }

    // devices
    private native long devices(long graph);
    public mpr.List<mpr.Device> devices()
        { return new mpr.List<mpr.Device>(devices(_graph)); }

    // signals
    private native long signals(long graph);
    public mpr.List<mpr.Signal> signals()
        { return new mpr.List<mpr.Signal>(signals(_graph)); }

    // maps
    private native long maps(long graph);
    public mpr.List<mpr.Map> maps()
        { return new mpr.List<mpr.Map>(maps(_graph)); }

    /* Note: this is _not_ guaranteed to run, the user should still call free()
     * explicitly when the graph is no longer needed. */
    protected void finalize() throws Throwable {
        try {
            free();
        } finally {
            super.finalize();
        }
    }

    private long _graph;
    private Set<Listener> _listeners;
    public boolean valid() {
        return _graph != 0;
    }

    static {
        System.loadLibrary(NativeLib.name);
    }
}
