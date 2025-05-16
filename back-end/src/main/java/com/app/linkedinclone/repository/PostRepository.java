package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Post;
import com.app.linkedinclone.model.dao.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByReactions_Author(User currentUser);
    Post findByReactions_AuthorAndId(User currentUser,Long id);
    Post findByComments_AuthorAndId(User currentUser,Long id);
    @Query("SELECT p FROM Post p WHERE p.author = :author OR p.author IN :network")
    Page<Post> findAllByAuthorAndNetwork(@Param("author") User author, @Param("network") Set<User> network, Pageable pageable);

    List<Post> findAllByAuthor(User user);
}
