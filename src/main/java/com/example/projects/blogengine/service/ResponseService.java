package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.repository.CommentRepository;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.utility.PageRequestWithOffset;
import com.example.projects.blogengine.repository.PostRepository;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ResponseService {

    private final Logger logger = LoggerFactory.getLogger(ResponseService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper mapper;

    public PostListResponse getPostList(int limit, int offset, String mode){
        //todo assert not null (offset)
        List<Post> posts;
        PostListResponse response = new PostListResponse();
        Pageable page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        switch (mode) {
            case "popular":
                posts = postRepository.getPopularPosts(page);
                break;
            case "best":
                posts = postRepository.getBestPosts(page);
                break;
            case "early":
                posts = postRepository.getEarlyPosts(page);
                break;
            default:
                posts = postRepository.getRecentPosts(page);
                break;
        }
        response.setCount(postRepository.getPostsCount());
        response.setPosts(convertToPostResponse(posts));
        return response;
    }

    private List<PostAnnounceResponse> convertToPostResponse(List<Post> posts){
        List<PostAnnounceResponse> response = new ArrayList<>();
        for (Post post : posts) {
            PostAnnounceResponse postAnnounce = mapper.map(post, PostAnnounceResponse.class);
            postAnnounce.setLikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
            postAnnounce.setDislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
            postAnnounce.setCommentCount(post.getComments().size());
            postAnnounce.setAnnounce(Jsoup.parse(post.getText()).text());
            postAnnounce.setTimestamp(post.getTime().toEpochSecond());
            response.add(postAnnounce);
        }
        return response;
    }


}
