package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.TagWeightResponse;
import com.example.projects.blogengine.api.response.TagsListResponse;
import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.repository.projections.TagStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    public TagsListResponse getTagList(String query) {
        TagsListResponse response = new TagsListResponse();
        List<Tag> tags;
        List<TagWeightResponse> tagWeightResponses = new ArrayList<>();
        if (query == null || query.equals("")){
            tags = tagRepository.getTags();
        } else {
            tags = tagRepository.getTagStartsWith(query);
        }
        OptionalInt max = tags.stream().mapToInt(tag -> tag.getPosts().size()).max();
        if (max.isEmpty()){
            return response;
        }
        if (max.getAsInt() == 0) return response;
        tags.forEach(tag -> tagWeightResponses.add(new TagWeightResponse(tag.getName(), (double) tag.getPosts().size() / max.getAsInt())));
        response.setTags(tagWeightResponses);
        return response;
    }

    public TagsListResponse getTagList1(String query) {
        TagsListResponse response = new TagsListResponse();
        List<TagWeightResponse> weights = new ArrayList<>();
        List<TagStatistics> tmps = tagRepository.getTagStatistics();
        for (TagStatistics tmp : tmps) {
            weights.add(new TagWeightResponse(tmp.getTagName(), tmp.getTagNorm()));
        }
        response.setTags(weights);
        return response;
    }
}
