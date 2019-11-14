
module org.praxislive.code {

    requires java.compiler;
    requires java.desktop;
    requires java.logging;

    requires org.praxislive.core;
    requires org.praxislive.logging;
    requires org.praxislive.util;
    
    exports org.praxislive.code;
    exports org.praxislive.code.userapi;

    provides javax.annotation.processing.Processor with 
            org.praxislive.code.internal.GenerateTemplateProcessor;
    provides org.praxislive.core.Port.TypeProvider with
            org.praxislive.code.DataPort.Provider;
    provides org.praxislive.core.Protocol.TypeProvider with
            org.praxislive.code.internal.CodeProtocolsProvider;
    
}
