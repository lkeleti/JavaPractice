package dev.lkeleti.blog.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String content;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Comment(String name, String content, Post post) {
        this.name = name;
        this.content = content;
        publishedOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
        this.post = post;
        post.addComment(this);
    }

    @PreUpdate
    public void updateUpdatedOn() {
        this.updatedOn = LocalDateTime.now();
    }
}
