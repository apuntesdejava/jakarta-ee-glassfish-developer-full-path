package com.mycompany.projecttracker.batch;


import com.mycompany.projecttracker.entity.Task;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Batch writer that persists processed task entities.
 */
@Named
@Dependent
public class TaskWriter extends AbstractItemWriter {

    /** Logger for batch write activity. */
    private static final Logger LOGGER = Logger.getLogger(TaskWriter.class.getName());

    /** Entity manager used by the batch chunk transaction. */
    @PersistenceContext
    private EntityManager em;

    /**
     * Persists one chunk of processed tasks.
     *
     * @param items processed task entities
     * @throws Exception when the batch runtime cannot write the chunk
     */
    @Override
    public void writeItems(List<Object> items) throws Exception {
        LOGGER.info("--> Batch Writer: Guardando chunk de " + items.size() + " tareas.");

        /*
         * Cada item ya fue validado y transformado por el processor, así que aquí se persiste
         * dentro de la transacción del chunk.
         */
        for (Object obj : items) {
            Task task = (Task) obj;
            em.persist(task);
        }
    }
}
