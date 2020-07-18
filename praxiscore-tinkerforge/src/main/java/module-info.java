
module org.praxislive.tinkerforge {

    requires java.logging;
    
    requires org.praxislive.base;
    requires org.praxislive.code;
    requires org.praxislive.core;

    requires com.tinkerforge;
    
    exports org.praxislive.tinkerforge;
    exports org.praxislive.tinkerforge.userapi;
    
    provides org.praxislive.core.services.ComponentFactoryProvider with
            org.praxislive.tinkerforge.components.TFComponents,
            org.praxislive.tinkerforge.TFRootProvider;
    
    opens org.praxislive.tinkerforge.components;
    opens org.praxislive.tinkerforge.components.resources;
}
