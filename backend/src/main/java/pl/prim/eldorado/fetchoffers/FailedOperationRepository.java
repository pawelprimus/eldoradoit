package pl.prim.eldorado.fetchoffers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.prim.eldorado.model.fails.FailedOperation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FailedOperationRepository extends JpaRepository<FailedOperation, Long> {
    List<FailedOperation> findByResolvedFalseOrderByTimestampDesc();
    List<FailedOperation> findByTimestampBeforeAndResolved(LocalDateTime timestamp, boolean resolved);
}