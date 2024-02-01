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
  "/api/member": {
    /** 내 정보 조회 */
    get: operations["detailMember"];
    /** 내 정보 수정 */
    put: operations["modifyMember"];
  };
  "/api/job-posts/{id}": {
    /** 구인공고 단건 조회 */
    get: operations["showDetailPost"];
    /** 구인공고 수정 */
    put: operations["modifyPost"];
    /** 구인공고 삭제 */
    delete: operations["deleteJobPost"];
  };
  "/api/applications/{id}": {
    /** 지원서 상세 내용 */
    get: operations["detailApplication"];
    /** 지원서 수정 */
    put: operations["modifyApplication"];
    /** 지원서 작성 */
    post: operations["writeApplication"];
    /** 지원서 삭제 */
    delete: operations["deleteApplication"];
  };
  "/api/post-comment/{postId}/comment": {
    /** 댓글 작성 */
    post: operations["write"];
  };
  "/api/member/login": {
    /** 로그인, accessToken 쿠키 생성됨 */
    post: operations["login"];
  };
  "/api/member/join": {
    /** 회원가입 */
    post: operations["join"];
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
    post: operations["approve"];
  };
  "/api/post-comment/{postId}": {
    /** 해당 공고에 달린 댓글 목록 */
    get: operations["findByPostId"];
  };
  "/api/member/myposts": {
    /** 내 공고 조회 */
    get: operations["detailMyPosts"];
  };
  "/api/member/myinterest": {
    /** 내 관심 공고 조회 */
    get: operations["detailMyInterestingPosts"];
  };
  "/api/member/mycomments": {
    /** 내 댓글 조회 */
    get: operations["detailMyComments"];
  };
  "/api/member/myapplications": {
    /** 내 지원서 조회 */
    get: operations["detailMyApplications"];
  };
  "/api/employ/{postId}": {
    get: operations["getList"];
  };
}

export type webhooks = Record<string, never>;

export interface components {
  schemas: {
    Register: {
      content: string;
    };
    Modify: {
      /** @enum {string} */
      gender?: "MALE" | "FEMALE" | "UNDEFINED";
      location?: string;
      /** Format: date */
      birth?: string;
      password?: string;
    };
    LoginForm: {
      username: string;
      password: string;
    };
    AuthAndMakeTokensResponse: {
      memberDto?: components["schemas"]["MemberDto"];
      accessToken?: string;
      refreshToken?: string;
    };
    MemberDto: {
      /** Format: int64 */
      id?: number;
      username?: string;
      /** @enum {string} */
      gender?: "MALE" | "FEMALE" | "UNDEFINED";
      location?: string;
      /** Format: date */
      birth?: string;
      name?: string;
      phoneNumber?: string;
    };
    RsDataAuthAndMakeTokensResponse: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["AuthAndMakeTokensResponse"];
      success?: boolean;
      fail?: boolean;
    };
    JoinForm: {
      username: string;
      password: string;
      name: string;
      phoneNumber: string;
      /** @enum {string} */
      gender?: "MALE" | "FEMALE" | "UNDEFINED";
      location: string;
      /** Format: date */
      birth: string;
    };
    CommentDto: {
      /** Format: int64 */
      id?: number;
      /** Format: int64 */
      jobPostId?: number;
      author?: string;
      content?: string;
      /** Format: date-time */
      createAt?: string;
      /** Format: date-time */
      modifyAt?: string;
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
      applicationCount: number;
      /** Format: int64 */
      interestsCount: number;
      createdAt?: string;
      closed?: boolean;
    };
    ApplicationDto: {
      /** Format: int64 */
      id?: number;
      author?: string;
      /** Format: int64 */
      postId?: number;
      body?: string;
      /** Format: date-time */
      createdAt?: string;
      approve?: boolean;
    };
    RsDataListJobPostDto: {
      resultCode?: string;
      /** Format: int32 */
      statusCode?: number;
      msg?: string;
      data?: components["schemas"]["JobPostDto"][];
      success?: boolean;
      fail?: boolean;
    };
    JobPostDetailDto: {
      /** Format: int64 */
      id: number;
      author: string;
      title: string;
      body: string;
      location: string;
      createdAt?: string;
      modifyAt?: string;
      closed?: boolean;
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
        content: {
          "*/*": string;
        };
      };
    };
  };
  /** 내 정보 조회 */
  detailMember: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["MemberDto"];
        };
      };
    };
  };
  /** 내 정보 수정 */
  modifyMember: {
    requestBody: {
      content: {
        "application/json": components["schemas"]["Modify"];
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
          "*/*": components["schemas"]["JobPostDetailDto"];
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
        content: never;
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
  /** 지원서 상세 내용 */
  detailApplication: {
    parameters: {
      path: {
        id: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["ApplicationDto"];
        };
      };
    };
  };
  /** 지원서 수정 */
  modifyApplication: {
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
        content: never;
      };
    };
  };
  /** 지원서 작성 */
  writeApplication: {
    parameters: {
      path: {
        id: number;
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
          "*/*": string;
        };
      };
    };
  };
  /** 지원서 삭제 */
  deleteApplication: {
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
          "*/*": string;
        };
      };
    };
  };
  /** 로그인, accessToken 쿠키 생성됨 */
  login: {
    requestBody: {
      content: {
        "application/json": components["schemas"]["LoginForm"];
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["RsDataAuthAndMakeTokensResponse"];
        };
      };
    };
  };
  /** 회원가입 */
  join: {
    requestBody: {
      content: {
        "application/json": components["schemas"]["JoinForm"];
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
          "*/*": string;
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
          "*/*": components["schemas"]["CommentDto"][];
        };
      };
    };
  };
  /** 내 공고 조회 */
  detailMyPosts: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["JobPostDto"][];
        };
      };
    };
  };
  /** 내 관심 공고 조회 */
  detailMyInterestingPosts: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["JobPostDto"][];
        };
      };
    };
  };
  /** 내 댓글 조회 */
  detailMyComments: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["CommentDto"][];
        };
      };
    };
  };
  /** 내 지원서 조회 */
  detailMyApplications: {
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["ApplicationDto"][];
        };
      };
    };
  };
  getList: {
    parameters: {
      path: {
        postId: number;
      };
    };
    responses: {
      /** @description OK */
      200: {
        content: {
          "*/*": components["schemas"]["ApplicationDto"][];
        };
      };
    };
  };
}
