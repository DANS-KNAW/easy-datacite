package nl.knaw.dans.easy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryLane
{

    private static final Logger logger = LoggerFactory.getLogger(MemoryLane.class);

    public static void main(String[] args)
    {

        int mb = 1024 * 1024;

        // Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB]");

        // Print used memory
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        // Print free memory
        System.out.println("Free Memory:" + runtime.freeMemory() / mb);

        // Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        // Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }

    public static void printMemory(String prefixMessage)
    {
        int mb = 1024 * 1024;
        logger.debug("begin: " + prefixMessage);

        // Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        logger.debug("##### Heap utilization statistics [MB] #####");

        // Print used memory

        logger.debug("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        // Print free memory
        logger.debug("Free Memory:" + runtime.freeMemory() / mb);

        // Print total available memory

        logger.debug("Total Memory:" + runtime.totalMemory() / mb);

        // Print Maximum available memory
        logger.debug("Max Memory:" + runtime.maxMemory() / mb);
        logger.debug("end: " + prefixMessage);
    }

}
