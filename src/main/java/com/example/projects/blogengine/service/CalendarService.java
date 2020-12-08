package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.CalendarResponse;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.projections.CalendarStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
public class CalendarService {
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public CalendarResponse getCalendarResponse(Integer year) {
        CalendarResponse response = new CalendarResponse();
        Stream<Post> postStream = postRepository.getPostsStream();
        Set<Integer> years = new HashSet<>();
        Map<String, Integer> postCount = new HashMap<>();
        postStream.forEach(post -> {
            String key = DateTimeFormatter.ISO_LOCAL_DATE.format(post.getTime());
            years.add(post.getTime().getYear());
            if (post.getTime().getYear() == year){
                if (postCount.containsKey(key)){
                    postCount.put(key, postCount.get(key) + 1);
                } else {
                    postCount.put(key, 1);
                }
            }
        });
        List<Integer> sortedYears = new ArrayList<>(years);
        sortedYears.sort(Integer::compareTo);
        response.setYears(sortedYears);
        response.setPosts(postCount);
        return response;
    }

    public CalendarResponse getCalendarResponse1(Integer year) {
        List<CalendarStatistics> postCountPerYear = postRepository.getPostCountPerDayInYear(year);
        List<Integer> years = postRepository.getYears();
        CalendarResponse response = new CalendarResponse();
        Map<String, Integer> posts = new HashMap<>();
        postCountPerYear.forEach(p -> posts.put(p.getDate(), p.getCount()));
        response.setPosts(posts);
        response.setYears(years);
        return response;
    }

}