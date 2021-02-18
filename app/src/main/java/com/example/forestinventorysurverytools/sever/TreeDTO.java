package com.example.forestinventorysurverytools.sever;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TreeDTO {

    private String tid; // 나무아이디
    private String dist; // 거리
    private String dbh; // 흉고직경
    private String height;  // 수고
    private String azimuth; //방위각
    private String latitude; // 위도
    private String longitude; // 경도
    private String pid; // 조사자
    private String imgPath; //실제 서버 DTO에서는 이미지패스추가
}