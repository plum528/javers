package org.javers.repository.api;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.collections.Predicate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.snapshot.SnapshotDiffer;

import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * @author bartosz walacik
 */
public class JaversExtendedRepository implements JaversRepository {
    private final JaversRepository delegate;
    private final SnapshotDiffer snapshotDiffer;

    public JaversExtendedRepository(JaversRepository delegate, SnapshotDiffer snapshotDiffer) {
        this.delegate = delegate;
        this.snapshotDiffer = snapshotDiffer;
    }

    public List<Change> getPropertyChangeHistory(GlobalId globalId, final String propertyName, int limit) {
        argumentsAreNotNull(globalId, propertyName);

        List<CdoSnapshot> snapshots = getPropertyStateHistory(globalId, propertyName, limit);
        List<Change> changes = snapshotDiffer.calculateDiffs(snapshots);

        return Lists.positiveFilter(changes, new Predicate<Change>() {
            public boolean apply(Change input) {
            return input instanceof PropertyChange && ((PropertyChange) input).getPropertyName().equals(propertyName);
            }
        });
    }

    public List<Change> getChangeHistory(GlobalId globalId, int limit) {
        argumentsAreNotNull(globalId);

        List<CdoSnapshot> snapshots = getStateHistory(globalId, limit);
        return snapshotDiffer.calculateDiffs(snapshots);
    }

    public List<Change> getChangeHistory(Class givenClass, int limit) {
        argumentsAreNotNull(givenClass);

        List<CdoSnapshot> snapshots = getStateHistory(givenClass, limit);
        return snapshotDiffer.calculateDiffs(snapshots);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        argumentIsNotNull(globalId);
        return delegate.getStateHistory(globalId, limit);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, int limit) {
        argumentsAreNotNull(globalId, propertyName);
        return delegate.getPropertyStateHistory(globalId, propertyName, limit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        argumentIsNotNull(globalId);
        return delegate.getLatest(globalId);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Class givenClass, int limit) {
        return delegate.getStateHistory(givenClass, limit);
    }

    @Override
    public void persist(Commit commit) {
        delegate.persist(commit);
    }

    @Override
    public CommitId getHeadId() {
        return delegate.getHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
    }

    @Override
    public void ensureSchema() {
        delegate.ensureSchema();
    }
}
