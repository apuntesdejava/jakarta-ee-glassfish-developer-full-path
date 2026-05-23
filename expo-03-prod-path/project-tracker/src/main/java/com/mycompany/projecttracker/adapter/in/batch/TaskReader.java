package com.mycompany.projecttracker.adapter.in.batch;

import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

@Named
@Dependent
public class TaskReader extends AbstractItemReader {

    private String[] rawCsvData;
    private int index;

    @Override
    public void open(java.io.Serializable checkpoint) throws Exception {
        rawCsvData = new String[]{
            "Importar Datos,Pendiente,1",
            "Analizar Logs,En Progreso,1",
            "Limpiar Cache,Completada,1",
            "Revisar Seguridad,Pendiente,1"
        };
        index = 0;
    }

    @Override
    public Object readItem() throws Exception {
        if (index < rawCsvData.length) {
            return rawCsvData[index++];
        }
        return null;
    }
}
