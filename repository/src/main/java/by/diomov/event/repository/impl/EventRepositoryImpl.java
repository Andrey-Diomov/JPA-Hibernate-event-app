package by.diomov.event.repository.impl;

import by.diomov.event.entity.Event;
import by.diomov.event.parameter.ParametersEventQuery;
import by.diomov.event.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@AllArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private static final String TOPIC = "topic";
    private static final String ORGANIZER = "organizer";
    private static final String TIME = "time";

    private static final String LIKE_OPERATOR_FORMAT = "%%%s%%";
    private static final String DEFAULT_SORTING_MODE = "ASC";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Event create(Event event) {
        entityManager.persist(event);
        return event;
    }

    @Override
    public Optional<Event> get(long id) {
        return Optional.ofNullable(entityManager.find(Event.class, id));
    }

    @Override
    public List<Event> get(ParametersEventQuery parameters) {
        CriteriaQuery<Event> criteriaQuery = createCriteriaQuery(parameters);
        return entityManager.createQuery(criteriaQuery)
                .getResultList();
    }

    @Override
    public Event patch(Event event, Event patch) {
        String topic = patch.getTopic();
        if (topic != null) {
            event.setTopic(topic);
        }
        String description = patch.getDescription();
        if (description != null) {
            event.setDescription(description);
        }
        String organizer = patch.getOrganizer();
        if (organizer != null) {
            event.setOrganizer(organizer);
        }

        String location = patch.getLocation();
        if (location != null) {
            event.setLocation(location);
        }
        return entityManager.merge(event);
    }

    @Override
    public void delete(long id) {
        Event event = entityManager.find(Event.class, id);
        entityManager.remove(event);
    }

    private CriteriaQuery<Event> createCriteriaQuery(
            ParametersEventQuery parameters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (isNotBlank(parameters.getTopic())) {
            predicates.add(builder.like(root.get(TOPIC),
                    format(LIKE_OPERATOR_FORMAT, parameters.getTopic())));
        }

        if (isNotBlank(parameters.getOrganizer())) {
            predicates.add(builder.like(root.get(ORGANIZER),
                    format(LIKE_OPERATOR_FORMAT, parameters.getOrganizer())));
        }

        if (isNotBlank(parameters.getSort())) {
            if (parameters.getSort().equalsIgnoreCase(DEFAULT_SORTING_MODE)) {
                query.orderBy(builder.asc(root.get(TIME)));
            } else {
                query.orderBy(builder.desc(root.get(TIME)));
            }
        }

        //query.orderBy(builder.asc(root.get(TOPIC)));
        return query.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
    }
}