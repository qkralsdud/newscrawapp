package com.cos.newscrawapp.batch;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cos.newscrawapp.domain.News;
import com.cos.newscrawapp.domain.NewsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class NewsBatch {
	
	private long aid = 278000;
	private final NewsRepository newsRepository;
	
	@Scheduled(cron = "0 3 19 * * *", zone = "Asia/Seoul")
	public void 네이버뉴스크롤링() {
		System.out.println("배치 프로그램 시작======================");
		List<News> naverNewsList = new ArrayList<>();
		int errorCount = 0;
		int successCount = 0;
		int crawCount = 0;
		while(true) {
			String aidStr = String.format("%010d", aid);
			System.out.println("aidStr : "+aidStr);
			String url = "https://news.naver.com/main/read.naver?mode=LSD&mid=shm&sid1=103&oid=437&aid="+aidStr;
			
			try {
				Document doc =  Jsoup.connect(url).get();
				// title, company, createdAt
				String title = doc.selectFirst("#articleTitle").text();
				String company = doc.selectFirst(".press_logo img").attr("alt");
				String createdAt = doc.selectFirst(".t11").text();
								
				LocalDate today = LocalDate.now();
				LocalDate yesterday = today.minusDays(1);
				//System.out.println("yesterday : "+yesterday);
				
				createdAt = createdAt.substring(0, 10);
				createdAt = createdAt.replace(".", "-");
				//System.out.println(createdAt);
				
				if(today.toString().equals(createdAt)) {
					System.out.println("createdAt : "+createdAt);
					break; 
				}
				
				if(yesterday.toString().equals(createdAt)) { 
					System.out.println("어제 기사입니다. 크롤링 잘 됨");
					
					naverNewsList.add(News.builder()
							.title(title)
							.company(company)
							.createdAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1).plusHours(9)))
							.build()
					);
					
					crawCount++;
				}
				
				successCount ++;
			} catch (Exception e) {
				System.out.println("해당 주소에 페이지를 찾을 수 없습니다 :  "+e.getMessage());
				errorCount ++;
			} 
			aid++;
			
		}	// end of while
		System.out.println("배치 프로그램 종료======================");
		System.out.println("성공횟수 : "+successCount);
		System.out.println("실패횟수 : "+errorCount);
		System.out.println("크롤링 성공횟수 : "+crawCount);
		System.out.println("마지막 aid 값 : "+aid);
		System.out.println("컬렉션에 담은 크기 : "+naverNewsList.size());
		// naverNewsRepository.saveAll(naverNewsList);
		
		Flux.fromIterable(naverNewsList)
			.flatMap(newsRepository::save)
			.subscribe(); 
			
	}

}
