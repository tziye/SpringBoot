package com.mongodb.transition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransitionService {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ProcessRepository processRepository;

    AtomicInteger counter = new AtomicInteger(0);

    public Process newProcess() {
        return processRepository.save(new Process(counter.incrementAndGet(), State.CREATED, 0));
    }

    @Transactional
    public void run(Integer id) {
        Process process = lookup(id);
        if (!State.CREATED.equals(process.getState())) {
            return;
        }
        start(process);
        verify(process);
        finish(process);
    }

    Process lookup(Integer id) {
        return processRepository.findById(id).get();
    }

    void start(Process process) {
        mongoTemplate.update(Process.class).matching(Query.query(Criteria.where("id").is(process.getId())))
                .apply(Update.update("state", State.ACTIVE).inc("transitionCount", 1)).first();
    }

    void verify(Process process) {
        Assert.state(process.getId() % 3 != 0, "We're sorry but we needed to drop that one");
    }

    void finish(Process process) {
        mongoTemplate.update(Process.class).matching(Query.query(Criteria.where("id").is(process.getId())))
                .apply(Update.update("state", State.DONE).inc("transitionCount", 1)).first();
    }

}
