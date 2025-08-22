declare namespace Api {
  /**
   * namespace Auth
   *
   * backend api module: "auth"
   */
  namespace Auth {
    interface LoginToken {
      token: string;
      refreshToken: string;
    }

    interface UserInfo {
      id: string;
      userName: string;
      nickName: string;
      realName: string;
      roleIds: string[];
      permissions: string[];
    }

    interface LoginHistory {
      id: string;
      loginTime: string;
      ipAddress: string;
      location: string;
      device: string;
      status: 'success' | 'failed';
      userAgent: string;
    }

    interface UpdateCurrentUserInfo {
      id: string;
      nickName: string;
      realName: string;
      avatar?: string;
      email?: string;
      phone?: string;
      gender: string;
      status: string;
    }
  }
}
