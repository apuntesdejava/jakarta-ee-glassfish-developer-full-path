package com.mycompany.projecttracker.batch;

import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * Batch reader that supplies raw task rows.
 */
@Named
@Dependent
public class TaskReader extends AbstractItemReader {

    /** In-memory task data used as the import source. */
    private String[] rawCsvData;

    /** Current read position in the source array. */
    private int index;

    /**
     * Initializes the import source and resets the cursor.
     *
     * @param checkpoint previous checkpoint, unused by this simple reader
     * @throws Exception when the batch runtime cannot open the reader
     */
    @Override
    public void open(java.io.Serializable checkpoint) throws Exception {
        /*
         * Para la demostración, las filas de entrada viven en memoria con formato
         * titulo,estado,projectId.
         */
        rawCsvData = new String[]{
            "Importar Datos,Pendiente,1",
            "Analizar Logs,En Progreso,1",
            "Limpiar Cache,Completada,1",
            "Revisar Seguridad,Pendiente,1"
        };
        index = 0;
    }

    /**
     * Reads the next raw row.
     *
     * @return next row or {@code null} when the source is exhausted
     * @throws Exception when the batch runtime cannot read the item
     */
    @Override
    public Object readItem() throws Exception {
        if (index < rawCsvData.length) {
            return rawCsvData[index++];
        }
        /*
         * Jakarta Batch interpreta null como fin del origen de datos.
         */
        return null;
    }
}
