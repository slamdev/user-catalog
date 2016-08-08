package com.github.slamdev.catalog.business.user.boundary;

import com.github.slamdev.catalog.business.user.control.UserResourceProvider;
import com.github.slamdev.catalog.business.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.github.slamdev.catalog.integration.HeaderUtils.selfLocation;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserResourceProvider resourceProvider;

    @Autowired
    private UserRepository repository;

    @RequestMapping(method = POST)
    public ResponseEntity<Void> create(@Valid @RequestBody User user) {
        repository.save(user);
        Resource<User> resource = resourceProvider.toResource(user);
        return new ResponseEntity<>(selfLocation(resource), CREATED);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Resource<User> get(@PathVariable long id) {
        return resourceProvider.toResource(ofNullable(repository.findOne(id)).orElseThrow(NotFoundException::new));
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (repository.exists(id)) {
            repository.delete(id);
            return new ResponseEntity<>(NO_CONTENT);
        }
        throw new NotFoundException();
    }

    @RequestMapping(method = GET)
    public Resources<Resource> getAll() {
        return resourceProvider.toResourcesWrapper(repository.findAll());
    }
}
