
module org.praxislive.data {
    requires org.praxislive.core;
    requires org.praxislive.base;
    
    provides org.praxislive.core.services.ComponentFactoryProvider with
            org.praxislive.data.DataRootProvider;
    
    opens org.praxislive.data to
            org.praxislive.base;
    
}
