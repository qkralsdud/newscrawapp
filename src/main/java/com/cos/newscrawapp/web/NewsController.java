package com.cos.newscrawapp.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.newscrawapp.domain.News;
import com.cos.newscrawapp.domain.NewsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@RestController
public class NewsController {
	
	private final NewsRepository newsRepository;
	
	@CrossOrigin
	@GetMapping(value = "/naverNews", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<News> home(){
		
		return newsRepository.mFindAll()
				.subscribeOn(Schedulers.boundedElastic()); 
	}

}
