package br.com.nlw.events.repository;

import br.com.nlw.events.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {

    public User findByEmail(String email);
}
