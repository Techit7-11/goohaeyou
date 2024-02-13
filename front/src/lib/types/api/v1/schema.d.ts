/**
 * This file was auto-generated by openapi-typescript.
 * Do not make direct changes to the file.
 */


export interface paths {
  "/api/post-comment/{postId}/comment/{commentId}": {
    /** 댓글 수정 */
    put: operations["modify"];
    /** 댓글 삭제 */
    delete: operations["delete"];
  };
  "/api/notification/{id}": {
    /** 알림 읽음 처리 */
    put: operations["read"];
  };
  "/api/job-posts/{id}": {
    /** 구인공고 단건 조회 */
    get: operations["showDetailPost"];
    /** 구인공고 수정 */
    put: operations["modifyPost"];
    /** 구인공고 삭제 */
    delete: operations["deleteJobPost"];
  };
  "/api/post-comment/{postId}/comment": {
    /** 댓글 작성 */
    post: operations["write"];
  };
  "/api/job-posts": {
    /** 구인공고 글 목록 가져오기 */
    get: operations["findAllPost"];
    /** 구인공고 작성 */
    post: operations["writePost"];
  };
  "/api/job-posts/{id}/interest": {
    /** 구인공고 관심 등록 */
    post: operations["increase"];
    /** 구인공고 관심 제거 */
    delete: operations["disinterest"];
  };
  "/api/employ/{postId}/{applicationIds}": {
    /** 지원서 승인 */
    patch: operations["approve"];
  };
  "/member/socialLogin/{providerTypeCode}": {
    /** 소셜 로그인 */
    get: operations["socialLogin"];
  };
  "/api/post-comment/{postId}": {
    /** 해당 공고에 달린 댓글 목록 */
    get: operations["findByPostId"];
  };
  "/api/notification": {
    /** 유저 별 알림리스트 */
    get: operations["getList"];
  };
  "/api/job-posts/sort": {
    /** 구인공고 글 목록 정렬 */
    get: operations["findAllPostSort"];
  };
  "/api/job-posts/search": {
    /** 게시물 검색 */
    get: operations["searchJobPostsByTitleAndBody"];
  };
  "/api/employ/{postId}": {
    /** 공고 별 지원리스트 */
    get: operations["getList_1"];
  };
  "/": {
    get: operations["showMain"];
  };
  "/api/notification/read": {
    /** 읽은 알림 전부 삭제 */
    delete: operations["deleteReadAll"];
  };
  "/api/notification/all": {
    /** 알림 전부 삭제 */
    delete: operations["deleteAll"];
  };
}

export type webhooks = Record<string, never>;

export interface components {
  schemas: {
    Register: {
      content: string;
    };
    Modify: {
      title: string;
      body: string;
      location: string;
      /** Format: int32 */
      minAge?: number;
      /** @enum {string} */
      gender?: "MALE" | "FEMALE" | "UNDEFINED";
      /** Format: date */
      deadLine: string;
    };
    RsDataModify: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["Modify"];
    };
    RsDataRegister: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["Register"];
    };
    CommentDto: {
      /** Format: int64 */
      id: number;
      /** Format: int64 */
      jobPostId: number;
      author: string;
      content: string;
      /** Format: date-time */
      createAt: string;
      /** Format: date-time */
      modifyAt: string;
    };
    RsDataListCommentDto: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["CommentDto"][];
    };
    NotificationDto: {
      /** Format: int64 */
      id?: number;
      createAt?: string;
      toMember?: string;
      fromMember?: string;
      relPostTitle?: string;
      /** @enum {string} */
      causeTypeCode?: "POST_MODIFICATION" | "POST_DELETED" | "POST_INTERESTED" | "POST_DEADLINE" | "COMMENT_CREATED" | "APPLICATION_CREATED" | "APPLICATION_MODIFICATION" | "APPLICATION_APPROVED" | "APPLICATION_UNAPPROVE";
      /** @enum {string} */
      resultTypeCode?: "NOTICE" | "DELETE" | "MODIFY";
      seen?: boolean;
      url?: string;
    };
    RsDataListNotificationDto: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["NotificationDto"][];
    };
    JobPostDto: {
      /** Format: int64 */
      id: number;
      author: string;
      title: string;
      location: string;
      /** Format: int64 */
      commentsCount: number;
      /** Format: int64 */
      incrementViewCount: number;
      /** Format: date */
      deadLine?: string;
      createdAt: string;
      closed?: boolean;
    };
    RsDataListJobPostDto: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["JobPostDto"][];
    };
    JobPostDetailDto: {
      /** Format: int64 */
      id: number;
      author: string;
      title: string;
      location: string;
      /** Format: int64 */
      commentsCount: number;
      /** Format: int64 */
      incrementViewCount: number;
      /** Format: date */
      deadLine?: string;
      createdAt: string;
      body: string;
      /** Format: int64 */
      applicationCount?: number;
      /** Format: int64 */
      interestsCount?: number;
      /** Format: int32 */
      minAge?: number;
      /** @enum {string} */
      gender?: "MALE" | "FEMALE" | "UNDEFINED";
      modifyAt?: string;
      employed?: boolean;
      closed?: boolean;
    };
    RsDataJobPostDetailDto: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["JobPostDetailDto"];
    };
    GetPostsResponseBody: {
      itemPage: components["schemas"]["PageDtoJobPostDto"];
    };
    PageDtoJobPostDto: {
      /** Format: int64 */
      totalElementsCount: number;
      /** Format: int64 */
      pageElementsCount: number;
      /** Format: int64 */
      totalPagesCount: number;
      /** Format: int32 */
      number: number;
      content: components["schemas"]["JobPostDto"][];
    };
    RsDataGetPostsResponseBody: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["GetPostsResponseBody"];
    };
    ApplicationDto: {
      /** Format: int64 */
      id: number;
      /** Format: int64 */
      jobPostId: number;
      jobPostAuthorUsername: string;
      jobPostName: string;
      author: string;
      /** Format: int64 */
      postId: number;
      body: string;
      /** Format: date */
      birth: string;
      phone: string;
      location: string;
      /** Format: date-time */
      createdAt?: string;
      approve?: boolean;
    };
    RsDataListApplicationDto: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["ApplicationDto"][];
    };
  };
  responses: never;
  parameters: never;
  requestBodies: never;
  headers: never;
  pathItems: never;
}

export type $defs = Record<string, never>;

export type external = Record<string, never>;

export interface operations {

  /** 댓글 수정 */
  modify: {
    parameters: {
      path: {
        postId: number;
        commentId: number;
      };
    };
    requestBody: {
      content: {
        "application/json": components["schemas"]["Register"];
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 댓글 삭제 */
  delete: {
    parameters: {
      path: {
        postId: number;
        commentId: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 알림 읽음 처리 */
  read: {
    parameters: {
      path: {
        id: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 구인공고 단건 조회 */
  showDetailPost: {
    parameters: {
      path: {
        id: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataJobPostDetailDto"];
        };
      };
    };
  };
  /** 구인공고 수정 */
  modifyPost: {
    parameters: {
      path: {
        id: number;
      };
    };
    requestBody: {
      content: {
        "application/json": components["schemas"]["Modify"];
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataModify"];
        };
      };
    };
  };
  /** 구인공고 삭제 */
  deleteJobPost: {
    parameters: {
      path: {
        id: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 댓글 작성 */
  write: {
    parameters: {
      path: {
        postId: number;
      };
    };
    requestBody: {
      content: {
        "application/json": components["schemas"]["Register"];
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataRegister"];
        };
      };
    };
  };
  /** 구인공고 글 목록 가져오기 */
  findAllPost: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataListJobPostDto"];
        };
      };
    };
  };
  /** 구인공고 작성 */
  writePost: {
    requestBody: {
      content: {
        "application/json": components["schemas"]["Register"];
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataRegister"];
        };
      };
    };
  };
  /** 구인공고 관심 등록 */
  increase: {
    parameters: {
      path: {
        id: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 구인공고 관심 제거 */
  disinterest: {
    parameters: {
      path: {
        id: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 지원서 승인 */
  approve: {
    parameters: {
      path: {
        postId: number;
        applicationIds: number[];
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 소셜 로그인 */
  socialLogin: {
    parameters: {
      query: {
        redirectUrl: string;
      };
      path: {
        providerTypeCode: string;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": string;
        };
      };
    };
  };
  /** 해당 공고에 달린 댓글 목록 */
  findByPostId: {
    parameters: {
      path: {
        postId: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataListCommentDto"];
        };
      };
    };
  };
  /** 유저 별 알림리스트 */
  getList: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataListNotificationDto"];
        };
      };
    };
  };
  /** 구인공고 글 목록 정렬 */
  findAllPostSort: {
    parameters: {
      query?: {
        page?: number;
        sortBy?: string[];
        sortOrder?: string[];
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataGetPostsResponseBody"];
        };
      };
    };
  };
  /** 게시물 검색 */
  searchJobPostsByTitleAndBody: {
    parameters: {
      query?: {
        titleOrBody?: string;
        title?: string;
        body?: string;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataListJobPostDto"];
        };
      };
    };
  };
  /** 공고 별 지원리스트 */
  getList_1: {
    parameters: {
      path: {
        postId: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataListApplicationDto"];
        };
      };
    };
  };
  showMain: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": string;
        };
      };
    };
  };
  /** 읽은 알림 전부 삭제 */
  deleteReadAll: {
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
  /** 알림 전부 삭제 */
  deleteAll: {
    responses: {
      /** @description OK */
      200: {
        content: never;
      };
    };
  };
}
