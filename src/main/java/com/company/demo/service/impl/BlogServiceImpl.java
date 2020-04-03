package com.company.demo.service.impl;

import com.company.demo.entity.Post;
import com.company.demo.exception.NotFoundException;
import com.company.demo.model.dto.PageableDto;
import com.company.demo.model.dto.PostInfoDto;
import com.company.demo.repository.PostRepository;
import com.company.demo.service.BlogService;
import com.company.demo.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import static com.company.demo.config.Constant.*;

@Component
public class BlogServiceImpl implements BlogService {
    @Autowired
    private PostRepository postRepository;

    @Override
    public Page<Post> getListPost(int page) {
        Page<Post> posts = postRepository.findAllByStatus(PUBLIC_POST, PageRequest.of(page, LIMIT_POST_PER_PAGE, Sort.by("publishedAt").descending().and(Sort.by("id").descending())));
        return posts;
    }

    @Override
    public Post getPostById(long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Không tìm thấy tin tức");
        }

        return post.get();
    }

    @Override
    public List<Post> getLatestPostsNotId(long id) {
        List<Post> posts = postRepository.getLatestPostsNotId(PUBLIC_POST, id, 8);
        return posts;
    }

    @Override
    public List<Post> getLatestPost() {
        List<Post> posts = postRepository.getLatestPosts(PUBLIC_POST, 8);
        return posts;
    }

    @Override
    public PageableDto adminGetListPost(String title, String status, int page, String order, String direction) {
        int limit = 15;
        PageUtil pageInfo  = new PageUtil(limit, page);

        // Get list posts and totalItems
        List<PostInfoDto> posts = postRepository.adminGetListPost(title, status, limit, pageInfo.calculateOffset(), order, direction);
        int totalItems = postRepository.countPostFilter(title, status);

        int totalPages = pageInfo.calculateTotalPage(totalItems);

        return new PageableDto(posts, totalPages, pageInfo.getPage());
    }
}
