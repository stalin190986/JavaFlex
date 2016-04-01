package Servicios;

/**
 * Representa la clase remota que permite conectarse con FLEX
 * mediante un destino definido en el archivo services-config.xml
 * @version 2.3
 * @author Arturo Hernándeez Peter
 * @see SurtidoService
 */
import flex.messaging.config.ConfigMap;
import flex.messaging.services.AbstractBootstrapService;
import flex.messaging.services.RemotingService;
import flex.messaging.services.remoting.RemotingDestination;

public class RemotingClass extends AbstractBootstrapService {

    private RemotingService remotingService;

    /**
     * This method is called by FDS when FDS has been initialized but not started.
     */
    public void initialize(String id, ConfigMap properties)
    {
        remotingService = (RemotingService) getMessageBroker().getService("remoting-service");
        RemotingDestination destination = (RemotingDestination) remotingService.createDestination(id);
        destination.setSource("Servicios.EntradasService");

    }

    /**
     * This method is called by FDS as FDS starts up (after initialization).
     */
    public void start()
    {
        // No-op
    }

    /**
     * This method is called by FDS as FDS shuts down.
     */
    public void stop()
    {
        // No-op
    }

}
