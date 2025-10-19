const local: App.I18n.Schema = {
  system: {
    title: 'Dynamic-Tp管理端',
    updateTitle: '系统版本更新通知',
    updateContent: '检测到系统有新版本发布，是否立即刷新页面？',
    updateConfirm: '立即刷新',
    updateCancel: '稍后再说'
  },
  common: {
    action: '操作',
    actionSuccess: '操作成功',
    add: '新增',
    addSuccess: '添加成功',
    backToHome: '返回首页',
    batchDelete: '批量删除',
    confirmBatchDelete: '确认批量删除吗？',
    cancel: '取消',
    close: '关闭',
    check: '勾选',
    expandColumn: '展开列',
    columnSetting: '列设置',
    config: '配置',
    confirm: '确认',
    delete: '删除',
    deleteSuccess: '删除成功',
    confirmDelete: '确认删除吗？',
    edit: '编辑',
    warning: '警告',
    error: '错误',
    index: '序号',
    keywordSearch: '请输入关键词搜索',
    logout: '退出登录',
    logoutConfirm: '确认退出登录吗？',
    lookForward: '敬请期待',
    modify: '修改',
    modifySuccess: '修改成功',
    noData: '无数据',
    operate: '操作',
    pleaseCheckValue: '请检查输入的值是否合法',
    refresh: '刷新',
    reset: '重置',
    search: '搜索',
    switch: '切换',
    tip: '提示',
    trigger: '触发',
    update: '更新',
    updateSuccess: '更新成功',
    save: '保存',
    saveSuccess: '保存成功',
    userCenter: '个人中心',
    openUp: '展开',
    putAway: '收起',
    yesOrNo: {
      yes: '是',
      no: '否'
    },
    moreOperation: '更多',
    createUser: '创建用户',
    createTime: '创建时间',
    updateUser: '更新用户',
    updateTime: '更新时间',
    isNotDevEnvTip: '当前非开发环境，无法进行此操作',
    uploadSuccess: '上传成功'
  },
  request: {
    logout: '请求失败后登出用户',
    logoutMsg: '用户状态失效，请重新登录',
    logoutWithModal: '请求失败后弹出模态框再登出用户',
    logoutWithModalMsg: '用户状态失效，请重新登录',
    refreshToken: '请求的token已过期，刷新token',
    tokenExpired: 'token已过期'
  },
  theme: {
    themeSchema: {
      title: '主题模式',
      light: '亮色模式',
      dark: '暗黑模式',
      auto: '跟随系统'
    },
    grayscale: '灰色模式',
    colourWeakness: '色弱模式',
    layoutMode: {
      title: '布局模式',
      vertical: '左侧菜单模式',
      'vertical-mix': '左侧菜单混合模式',
      horizontal: '顶部菜单模式',
      'horizontal-mix': '顶部菜单混合模式',
      reverseHorizontalMix: '一级菜单与子级菜单位置反转'
    },
    recommendColor: '应用推荐算法的颜色',
    recommendColorDesc: '推荐颜色的算法参照',
    themeColor: {
      title: '主题颜色',
      primary: '主色',
      info: '信息色',
      success: '成功色',
      warning: '警告色',
      error: '错误色',
      followPrimary: '跟随主色'
    },
    scrollMode: {
      title: '滚动模式',
      wrapper: '外层滚动',
      content: '主体滚动'
    },
    page: {
      animate: '页面切换动画',
      mode: {
        title: '页面切换动画类型',
        'fade-slide': '滑动',
        fade: '淡入淡出',
        'fade-bottom': '底部消退',
        'fade-scale': '缩放消退',
        'zoom-fade': '渐变',
        'zoom-out': '闪现',
        none: '无'
      }
    },
    fixedHeaderAndTab: '固定头部和标签栏',
    header: {
      height: '头部高��',
      breadcrumb: {
        visible: '显示面包屑',
        showIcon: '显示面包屑图标'
      },
      multilingual: {
        visible: '显示多语言按钮'
      },
      globalSearch: {
        visible: '显示全局搜索按钮'
      }
    },
    tab: {
      visible: '显示标签栏',
      cache: '标签栏信息缓存',
      height: '标签栏高度',
      mode: {
        title: '标签栏风格',
        chrome: '谷歌风格',
        button: '按钮风格'
      }
    },
    sider: {
      inverted: '深色侧边栏',
      width: '侧边栏宽度',
      collapsedWidth: '侧边栏折叠宽度',
      mixWidth: '混合布局侧边栏宽度',
      mixCollapsedWidth: '混合布局侧边栏折叠宽度',
      mixChildMenuWidth: '混合布局子菜单宽度'
    },
    footer: {
      visible: '显示底部',
      fixed: '固定底部',
      height: '底部高度',
      right: '底部局右'
    },
    watermark: {
      visible: '显示全屏水印',
      text: '水印文本',
      enableUserName: '启用用户名水印'
    },
    themeDrawerTitle: '主题配置',
    pageFunTitle: '页面功能',
    resetCacheStrategy: {
      title: '重置缓存策略',
      close: '关闭页面',
      refresh: '刷新页面'
    },
    configOperation: {
      copyConfig: '复制配置',
      copySuccessMsg: '复制成功，请替换 src/theme/settings.ts 中的变量 themeSettings',
      resetConfig: '重置配置',
      resetSuccessMsg: '重置成功'
    }
  },
  route: {
    login: '登录',
    403: '无权限',
    404: '页面不存在',
    500: '服务器错误',
    'iframe-page': '外链页面',
    home: '首页',
    document: '文档',
    document_project: '项目文档',
    'document_project-link': '项目文档(外链)',
    document_vue: 'Vue文档',
    document_vite: 'Vite文档',
    document_unocss: 'UnoCSS文档',
    document_naive: 'Naive UI文档',
    document_antd: 'Ant Design Vue文档',
    'user-center': '个人中心',
    about: '关于',
    function: '系统功能',
    function_tab: '标签页',
    'function_multi-tab': '多标签页',
    'function_hide-child': '隐藏子菜单',
    'function_hide-child_one': '隐藏子菜单',
    'function_hide-child_two': '菜单二',
    'function_hide-child_three': '菜单三',
    function_request: '请求',
    'function_toggle-auth': '切换权限',
    'function_super-page': '超级管理员可见',
    manage: '系统管理',
    manage_user: '用户管理',
    'manage_user-detail': '用户详情',
    manage_role: '角色管理',
    manage_menu: '菜单管理',
    manage_dict: '字典管理',
    manage_org: '组织管理',
    service: '服务管理',
    'thread-pool': '线程池管理',
    'notify-platform': '告警渠道管理',
    monitor: '日志管理',
    logs: '日志管理',
    logs_login: '登录日志',
    logs_operation: '操作日志',
    logs_error: '错误日志',

    'multi-menu': '多级菜单',
    'multi-menu_first': '菜单一',
    'multi-menu_first_child': '菜单一子菜单',
    'multi-menu_second': '菜单二',
    'multi-menu_second_child': '菜单二子菜单',
    'multi-menu_second_child_home': '菜单二子菜单首页',
    exception: '异常页',
    exception_403: '403',
    exception_404: '404',
    exception_500: '500'
  },
  page: {
    login: {
      common: {
        loginOrRegister: '登录 / 注册',
        userNamePlaceholder: '请输入用户名',
        phonePlaceholder: '请输入手机号',
        codePlaceholder: '请输入验证码',
        passwordPlaceholder: '请输入密码',
        confirmPasswordPlaceholder: '请再次输入密码',
        codeLogin: '验证码登录',
        confirm: '确定',
        back: '返回',
        validateSuccess: '验证成功',
        loginSuccess: '登录成功',
        welcomeBack: '欢迎回来，{userName} ！'
      },
      pwdLogin: {
        title: '密码登录',
        rememberMe: '记住我',
        forgetPassword: '忘记密码？',
        register: '注册账号',
        otherAccountLogin: '其他账号登录',
        otherLoginMode: '其他登录方式',
        superAdmin: '超级管理员',
        admin: '管理员',
        user: '普通用户'
      },
      codeLogin: {
        title: '验证码登录',
        getCode: '获取验证码',
        reGetCode: '{time}秒后重新获取',
        sendCodeSuccess: '验证码发送成功',
        imageCodePlaceholder: '请输入图片验证码'
      },
      register: {
        title: '注册账号',
        agreement: '我已经仔细阅读并接受',
        protocol: '《用户协议》',
        policy: '《隐私权政策》'
      },
      resetPwd: {
        title: '重置密码'
      },
      bindWeChat: {
        title: '绑定微信'
      }
    },
    about: {
      title: '关于',
      introduction: 'DynamicTp的Admin端',
      projectInfo: {
        title: '项目信息',
        version: '版本',
        latestBuildTime: '最新构建时间',
        githubLink: 'Github 地址',
        previewLink: '预览地址'
      },
      prdDep: '生产依赖',
      devDep: '开发依赖'
    },
    home: {
      greeting: '早安，{userName}, 今天又是充满活力的一天!',
      weatherDesc: '今日多云转晴，20℃ - 25℃!',
      projectCount: '项目数',
      todo: '待办',
      message: '消息',
      downloadCount: '下载量',
      registerCount: '注册量',
      schedule: '作息安排',
      study: '学习',
      work: '工作',
      rest: '休息',
      entertainment: '娱乐',
      visitCount: '访问量',
      turnover: '成交额',
      dealCount: '成交量',
      projectNews: {
        title: '项目动态',
        moreNews: '更多动态',
        desc1: 'Soybean 在2021年5月28日创建了开源项目 soybean-admin!',
        desc2: 'Yanbowe 向 soybean-admin 提交了一个bug，多标签栏不会自适应。',
        desc3: 'Soybean 准备为 soybean-admin 的发布做充分的准备工作!',
        desc4: 'Soybean 正在忙于为soybean-admin写项目说明文档！',
        desc5: 'Soybean 刚才把工作台页面随便写了一些，凑合能看了！'
      },
      creativity: '创意'
    },
    function: {
      tab: {
        tabOperate: {
          title: '标签页操作',
          addTab: '添加标签页',
          addTabDesc: '跳转到关于页面',
          closeTab: '关闭标签页',
          closeCurrentTab: '关闭当前标签页',
          closeAboutTab: '关闭"关于"标签页',
          addMultiTab: '添加多标签页',
          addMultiTabDesc1: '跳转到多标签页页面',
          addMultiTabDesc2: '跳转到多标签页页面(带有查询参数)'
        },
        tabTitle: {
          title: '标签页标题',
          changeTitle: '修改标题',
          change: '修改',
          resetTitle: '重置标题',
          reset: '重置'
        }
      },
      multiTab: {
        routeParam: '路由参数',
        backTab: '返回 function_tab'
      },
      toggleAuth: {
        toggleAccount: '切换账号',
        authHook: '权限钩子函数 `hasAuth`',
        superAdminVisible: '超级管理员可见',
        adminVisible: '管理员可见',
        adminOrUserVisible: '管理员和用户可见'
      },
      request: {
        repeatedErrorOccurOnce: '重复请求错误只出现一次',
        repeatedError: '重复请求错误',
        repeatedErrorMsg1: '自定义请求错误 1',
        repeatedErrorMsg2: '自定义请求错误 2'
      }
    },
    tools: {
      common: {
        renderType: {
          input: '输入框',
          select: '下拉框',
          radio: '单选框'
        },
        searchType: {
          equal: '等于',
          noEqual: '不等于',
          like: '模糊',
          leftLike: '左模糊',
          rightLike: '右模糊',
          greaterThan: '大于',
          greaterThanOrEqual: '大于等于',
          lessThan: '小于',
          lessThanOrEqual: '小于等于',
          in: '包含',
          notIn: '不包含',
          between: '在区间内',
          notBetween: '不在区间内'
        }
      }
    },
    manage: {
      common: {
        status: {
          enable: '启用',
          disable: '禁用'
        }
      },
      role: {
        title: '角色列表',
        roleName: '角色名称',
        roleCode: '角色编码',
        status: '角色状态',
        sort: '排序',
        description: '角色描述',
        menuAuth: '菜单权限',
        buttonAuth: '按钮权限',
        dataScopeAuth: '数据权限',
        form: {
          roleName: '请输入角色名称',
          roleCode: '请输入角色编码',
          status: '请选择角色状态',
          sort: '请填写角色排序',
          description: '请输入角色描述'
        },
        addRole: '新增角色',
        editRole: '编辑角色'
      },
      user: {
        title: '用户列表',
        userName: '用户名',
        gender: '性别',
        nickName: '昵称',
        realName: '真实姓名',
        phone: '手机号',
        email: '邮箱',
        status: '用户状态',
        userRole: '用户角色',
        userPosition: '职位',
        userOrgUnits: '用户组织',
        form: {
          userName: '请输入用户名',
          gender: '请选择性别',
          nickName: '请输入昵称',
          realName: '请输入真实姓名',
          phone: '请输入手机号',
          email: '请输入邮箱',
          status: '请选择用户状态',
          userRole: '请选择用户角色',
          userPosition: '请输入职位',
          userOrgUnits: '请选择用户组织'
        },
        addUser: '新增用户',
        editUser: '编辑用户',
        resetPwd: '重置密码',
        confirmResetPwd: '确认重置密码吗？',
        responsibilities: '职责设置',
        manageOrganization: '管理组织（负责人）',
        userGender: {
          confidential: '保密',
          male: '男',
          female: '女'
        },
        selectTreeIsEmptyTip: '请选择组织查看用户列表'
      },
      menu: {
        home: '首页',
        title: '菜单列表',
        id: 'ID',
        parentId: '父级菜单ID',
        type: '菜单类型',
        name: '菜单名称',
        routeName: '路由名称',
        routePath: '路由路径',
        pathParam: '路径参数',
        layout: '布局',
        page: '页面组件',
        i18nKey: '国际化key',
        icon: '图标',
        localIcon: '本地图标',
        iconTypeTitle: '图标类型',
        sort: '排序',
        keepAlive: '缓存路由',
        href: '外链',
        iframeUrl: '内嵌链接',
        hideInMenu: '隐藏菜单',
        activeMenu: '高亮的菜单',
        multiTab: '支持多页签',
        fixedIndexInTab: '固定在页签中的序号',
        query: '路由参数',
        status: '菜单状态',
        detail: '菜单详情',
        form: {
          home: '请选择首页',
          type: '请选择菜单类型',
          name: '请输入菜单名称',
          routeName: '请输入路由名称',
          routePath: '请输入路由路径',
          pathParam: '请输入路径参数',
          page: '请选择页面组件',
          layout: '请选择布局组件',
          i18nKey: '请输入国际化key',
          icon: '请输入图标',
          localIcon: '请选择本地图标',
          sort: '请输入排序',
          keepAlive: '请选择��否缓存路由',
          href: '请输入外链',
          iframeUrl: '请输入内嵌链接',
          hideInMenu: '请选择是否隐藏菜单',
          activeMenu: '请选择高亮的菜单的路由名称',
          multiTab: '请选择是否支持多标签',
          fixedInTab: '请选择是否固定在页签中',
          fixedIndexInTab: '请输入固定在页签中的序号',
          query: '请输入路由参数',
          queryKey: '请输入路由参数Key',
          queryValue: '请输入路由参数Value',
          status: '请选择菜单状态'
        },
        addMenu: '新增菜单',
        editMenu: '编辑菜单',
        addChildMenu: '新增子菜单',
        menuType: {
          directory: '目录',
          menu: '菜单'
        },
        iconType: {
          iconify: 'iconify图标',
          local: '本地图标'
        },
        selectTreeIsEmptyTip: '请选择菜单查看详情',
        menuTypeIsDirectory: '目录类型无需显示数据'
      },
      permission: {
        title: '按钮权限列表',
        menuName: '菜单名称',
        name: '按钮名称',
        resource: '权限资源',
        status: '按钮状态',
        sort: '排序',
        description: '按钮描述',
        addButton: '新增按钮',
        editButton: '编辑按钮',
        dataRules: '数据规则',
        form: {
          menuName: '请输入菜单名称',
          name: '请输入按钮名称',
          resource: '请输入权限资源',
          resourceIntroduction:
            '此按钮可关联多个后端接口权限。请添加当前按钮所需的每个权限。确保在用户操作该按钮时，系统会验证所有列出的接口权限，实现细粒度的权限管理。请确保您输入的权限准确无误，以免影响功能的正常使用。\n例如更新用户涉及两个接口，则需要添加两个，分别是：sys:user:get;sys:user:update',
          status: '请选择按钮状态',
          sort: '请输入排序',
          description: '请输入按钮描述'
        }
      },
      dict: {
        title: '字典列表',
        name: '字典名称',
        code: '字典编码',
        type: '字典类型',
        description: '字典描述',
        sort: '排序',
        status: '字典状态',
        form: {
          name: '请输入字典名称',
          code: '请输入字典编码',
          type: '请选择字典类型',
          description: '请输入字典描述',
          sort: '请输入排序',
          status: '请选择字典状态'
        },
        addDict: '新增字典',
        editDict: '编辑字典',
        dictType: {
          system: '系统字典',
          business: '业务字典'
        },
        loadCacheSuccess: '缓存成功',
        selectTreeIsEmptyTip: '请选择字典查看子项目',
        systemFieldsCannotBeDeleted: '系统字段禁止操作删除'
      },
      dictItem: {
        title: '字典项列表',
        dictCode: '字典编码',
        value: '字典值',
        zhCN: '中文',
        enUS: '英文',
        color: '#颜色',
        sort: '排序',
        type: '类型',
        status: '状态',
        description: '描述',
        form: {
          value: '请输入字典值',
          zhCN: '请输入字典项中文',
          enUS: '请输入字典项英文',
          color: '请选择颜色',
          type: '请选择类型',
          sort: '请输入排���',
          status: '请选择字典项状态',
          description: '请输入字典描述'
        },
        addDictItem: '新增字典项',
        editDictItem: '编辑字典项'
      },
      orgUnits: {
        title: '组织列表',
        name: '组织名称',
        code: '组织编码',
        abbr: '组织简称',
        description: '组织描述',
        sort: '排序',
        status: '组织状态',
        form: {
          name: '请输入组织名称',
          code: '请输入组织编码',
          abbr: '请输入组织简称',
          description: '请输入组织描述',
          sort: '请输入排序',
          status: '请选择组织状态'
        },
        addOrgUnits: '新增组织',
        editOrgUnits: '编辑组织',
        addChildOrgUnits: '新增子组织',
        unassigned: '未分配组织'
      },
      dataScope: {
        title: '数据权限列表',
        name: '名称',
        code: '编码',
        menuName: '菜单名称',
        permissionResource: '权限资源',
        permissionName: '权限名称',
        scopeType: '类型',
        scopeTypeName: '类型名称',
        customFields: '自定义字段',
        customRules: '自定义规则',
        description: '描述',
        sort: '排序',
        status: '状态',
        form: {
          title: '数据权限',
          name: '请输入名称',
          code: '请输入编码',
          menuName: '请选择菜单名称',
          permissionResource: '请选择权限资源',
          permissionName: '请选择权限名称',
          scopeType: '请选择类型',
          scopeTypeName: '请输入类型名称',
          customFields: '请输入自定义字段',
          customRules: '请输入自定义规则',
          description: '请输入描述',
          sort: '请输入排序',
          status: '请选择状态',
          field: '请输入字段',
          operator: '请选择操作符',
          value: '请输入值',
          logic: '请选择逻辑运算符',
          variable: '系统变量'
        }
      }
    },
    servicePage: {
      title: '服务',
      pageDescription: '管理和监控服务实例状态',
      serviceDetailDescription: '服务实例详细信息',
      totalInstances: '实例总数',
      onlineInstances: '在线实例',
      offlineInstances: '离线实例',
      instanceList: '实例列表',
      online: '在线',
      offline: '离线',
      moreInstances: '还有 {count} 个实例',
      noInstanceData: '暂无实例数据',
      noServiceData: '暂无服务数据',
      refresh: '刷新',
      clientId: '客户端ID',
      applicationName: '应用名称',
      lastHeartbeat: '最后心跳时间',
      registerTime: '注册时间',
      unknown: '未知',
      statusFilter: {
        all: '全部',
        online: '在线',
        offline: '离线'
      },
      searchPlaceholder: '请输入关键词',
      actions: {
        search: '搜索',
        refresh: '刷新',
        viewDetail: '查看详情'
      },
      serviceName: '服务名称'
    },logs: {
      login: {
        userName: '用户名',
        userRealName: '真实姓名',
        ip: '登录 IP',
        ipAddr: 'IP 所属地',
        userAgent: 'User-Agent',
        status: '登录状态',
        message: '登录信息',
        createTime: '登录时间',
        createUser: '登录用户',
        form: {
          userName: '请填写用户名',
          userRealName: '请填写真实姓名'
        },
        loginStatus: {
          fail: '登录失败',
          success: '登录成功'
        }
      },
      operation: {
        requestId: '请求 ID',
        ip: '操作 IP',
        ipAddr: 'IP 所属地',
        userAgent: 'User-Agent',
        requestUri: '请求 URI',
        requestMethod: '请求方式',
        contentType: '请求类型',
        methodName: '操作方法',
        operation: '操作方法说明',
        methodParams: '操作参数',
        useTime: '耗时(ms)',
        createUser: '操作用户',
        createTime: '操作时间',
        form: {
          createUser: '请填写操作用户'
        }
      },
      error: {
        requestId: '请求 ID',
        ip: '操作 IP',
        ipAddr: 'IP 所属地',
        userAgent: 'User-Agent',
        requestUri: '请求 URI',
        requestMethod: '请求方式',
        contentType: '请求类型',
        methodName: '操作方法',
        operation: '操作方法说明',
        methodParams: '操作参数',
        useTime: '耗时(ms)',
        createUser: '操作用户',
        createTime: '异常时间',
        exceptionMessage: '异常信息',
        exceptionClass: '异常类名',
        line: '异常行号',
        stackTrace: '异常堆栈',
        form: {
          createUser: '请填写操作用户'
        }
      }
    },
    monitor: {
      percentage: '{value} %',
      logs: {
        login: {
          userName: '用户名',
          userRealName: '真实姓名',
          ip: '登录 IP',
          ipAddr: 'IP 所属地',
          userAgent: 'User-Agent',
          status: '登录状态',
          message: '登录信息',
          createTime: '登录时间',
          createUser: '登录用户',
          form: {
            userName: '请填写用户名',
            userRealName: '请填写真实姓名'
          },
          loginStatus: {
            fail: '登录失败',
            success: '登录成功'
          }
        },
        operation: {
          requestId: '请求 ID',
          ip: '操作 IP',
          ipAddr: 'IP 所属地',
          userAgent: 'User-Agent',
          requestUri: '请求 URI',
          requestMethod: '请求方式',
          contentType: '请求类型',
          methodName: '操作方法',
          operation: '操作方法说明',
          methodParams: '操作参数',
          useTime: '耗时(ms)',
          createUser: '操作用户',
          createTime: '操作时间',
          form: {
            createUser: '请填写操作用户'
          }
        },
        error: {
          requestId: '请求 ID',
          ip: '操作 IP',
          ipAddr: 'IP 所属地',
          userAgent: 'User-Agent',
          requestUri: '请求 URI',
          requestMethod: '请求方式',
          contentType: '请求类型',
          methodName: '操作方法',
          operation: '操作方法说明',
          methodParams: '操作参数',
          useTime: '耗时(ms)',
          createUser: '操作用户',
          createTime: '异常时间',
          exceptionMessage: '异常信息',
          exceptionClass: '异常类名',
          line: '异常行号',
          stackTrace: '异常堆栈',
          form: {
            createUser: '请填写操作用户'
          }
        }
      },
      file: {
        orderNo: '订单号码',
        category: '分类',
        location: '存储位置',
        name: '文件名称',
        suffix: '文件尾缀',
        path: '文件路径',
        size: '文件大小',
        uuid: '文件UUID',
        contentType: '文件类型',
        remark: '备注',
        form: {
          orderNo: '请输入订单号码',
          category: '请选择分类',
          location: '请选择存储位置',
          name: '请输入文件名称'
        },
        upload: '上传文件',
        uploadSuccess: '上传成功',
        preview: '预览'
      }
    }
  },
  form: {
    required: '不能为空',
    userName: {
      required: '请输入用户名',
      invalid: '用户名格式不正确'
    },
    phone: {
      required: '请输入手机号',
      invalid: '手机号格式不正确'
    },
    pwd: {
      required: '请输入密码',
      invalid: '密码格式不正确，6-18位字符，包含字母、数字、下划线'
    },
    confirmPwd: {
      required: '请输入确认密码',
      invalid: '两次输入密码不一致'
    },
    code: {
      required: '请输入验证码',
      invalid: '验证码格式不正确'
    },
    email: {
      required: '请输入邮箱',
      invalid: '邮箱格式不正确'
    }
  },
  dropdown: {
    closeCurrent: '关闭',
    closeOther: '关闭其它',
    closeLeft: '关闭左侧',
    closeRight: '关闭右侧',
    closeAll: '关闭所有'
  },
  icon: {
    themeConfig: '主题配置',
    themeSchema: '主题模式',
    lang: '切换语言',
    fullscreen: '全屏',
    fullscreenExit: '退出全屏',
    reload: '刷新页面',
    collapse: '折叠菜单',
    expand: '展开菜单',
    pin: '固定',
    unpin: '取消固定'
  },
  datatable: {
    itemCount: '共 {total} 条'
  }
};

export default local;
