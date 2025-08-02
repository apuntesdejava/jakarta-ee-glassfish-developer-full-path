package com.tuempresa.proyecto.service;
import com.tuempresa.proyecto.domain.Project;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped // Indica que esta clase es un EJB Stateless, gestionado por el contenedor
public class ProjectService {

    @PersistenceContext(unitName = "pm-pu") // Inyecta el EntityManager para nuestra unidad de persistencia
    private EntityManager em;

    /**
     * Crea un nuevo proyecto en la base de datos.
     * @param project El objeto Project a persistir.
     * @return El proyecto persistido (con ID generado si aplica).
     */
    public Project createProject(Project project) {
        // 'persist' hace que una nueva entidad pase al estado gestionado y se prepare para ser insertada.
        em.persist(project);
        return project; // El ID se asigna al objeto Project después de persistir
    }

    /**
     * Busca un proyecto por su ID.
     * @param id El ID del proyecto.
     * @return El proyecto encontrado o null si no existe.
     */
    public Project findProjectById(Long id) {
        // 'find' busca una entidad por su clave primaria.
        return em.find(Project.class, id);
    }

    /**
     * Actualiza un proyecto existente en la base de datos.
     * @param project El objeto Project con los datos actualizados.
     * @return El proyecto actualizado (puede ser la misma instancia o una gestionada por JPA).
     */
    public Project updateProject(Project project) {
        // 'merge' se usa para entidades que podrían no estar en el contexto de persistencia.
        // Si la entidad ya existe y está gestionada, la sincroniza. Si no, la adjunta.
        return em.merge(project);
    }

    /**
     * Elimina un proyecto de la base de datos.
     * @param id El ID del proyecto a eliminar.
     */
    public void deleteProject(Long id) {
        Project project = findProjectById(id);
        if (project != null) {
            // 'remove' elimina la entidad del contexto de persistencia y la marca para eliminación de la BD.
            em.remove(project);
        }
    }

    /**
     * Obtiene todos los proyectos de la base de datos.
     * @return Una lista de todos los proyectos.
     */
    public List<Project> findAllProjects() {
        // Se utiliza JPQL (Jakarta Persistence Query Language) para consultar entidades.
        // "FROM Project" significa seleccionar todas las instancias de la entidad Project.
        return em.createQuery("SELECT p FROM Project p", Project.class).getResultList();
    }
}