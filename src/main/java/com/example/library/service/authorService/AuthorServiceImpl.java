package com.example.library.service.authorService;

import com.example.library.dao.authorDao.AuthorDAO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService{

    @Autowired
    private AuthorDAO authorDAO;

    @Override
    public List<Author> findAll() {
        return authorDAO.findAll();
    }

    @Override
    public Author findById(long id) {
        Optional<Author> result = Optional.ofNullable(authorDAO.findById(id));
        if(result.isPresent())
            return result.get();
        else
            throw new RuntimeException("Did not find Author id: " + id);
    }

    @Override
    public Author findByName(String name) {
        return authorDAO.findByName(name);
    }

    @Override
    public void save(Author author) {
        authorDAO.save(author);
    }

    @Override
    public void deleteById(long id) {
        authorDAO.deleteById(id);
    }
}
