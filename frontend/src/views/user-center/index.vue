<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { fetchChangePassword, fetchGetLoginHistory, fetchUpdateCurrentUserInfo } from '@/service/api';
import { useAuthStore } from '@/store/modules/auth';
import { useFormRules } from '@/hooks/common/form';

defineOptions({
  name: 'UserCenter'
});

const authStore = useAuthStore();

// 个人信息表单
const profileFormRef = ref();
const profileModel = reactive({
  nickName: authStore.userInfo.nickName || '',
  realName: authStore.userInfo.realName || '',
  gender: '0' as Api.SystemManage.UserGender,
  phone: '',
  email: ''
});

const profileRules = computed(() => {
  const { formRules } = useFormRules();
  return {
    nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
    phone: formRules.phone,
    email: formRules.email
  };
});

// 修改密码表单
const passwordFormRef = ref();
const passwordModel = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const passwordRules = computed(() => {
  const { formRules, createConfirmPwdRule } = useFormRules();
  return {
    oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
    newPassword: formRules.pwd,
    confirmPassword: createConfirmPwdRule(passwordModel.newPassword)
  };
});

// 活跃标签页
const activeTab = ref('profile');

// 提交个人信息
async function handleUpdateProfile() {
  try {
    await profileFormRef.value?.validate();
    const { error, data } = await fetchUpdateCurrentUserInfo({
      id: authStore.userInfo.id,
      nickName: profileModel.nickName,
      realName: profileModel.realName,
      gender: profileModel.gender,
      phone: profileModel.phone,
      email: profileModel.email,
      status: '1'
    });

    if (!error && data) {
      window.$message?.success('个人信息更新成功');
      // 更新store中的用户信息
      Object.assign(authStore.userInfo, {
        nickName: profileModel.nickName,
        realName: profileModel.realName
      });
    } else {
      window.$message?.error('个人信息更新失败');
    }
  } catch (error) {
    console.error('更新个人信息失败:', error);
    window.$message?.error('个人信息更新失败');
  }
}

// 修改密码
async function handleChangePassword() {
  try {
    await passwordFormRef.value?.validate();

    const { error, data } = await fetchChangePassword(passwordModel.oldPassword, passwordModel.newPassword);

    if (!error && data) {
      window.$message?.success('密码修改成功');

      // 清空表单
      Object.assign(passwordModel, {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
    } else {
      window.$message?.error('密码修改失败，请检查当前密码是否正确');
    }

    // 清空表单
    Object.assign(passwordModel, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    });
  } catch (error) {
    console.error('修改密码失败:', error);
    window.$message?.error('密码修改失败');
  }
}

// 登录记录数据
const loginHistoryData = ref<Api.Monitor.LoginHistory[]>([]);
const loadingHistory = ref(false);
const loginHistoryParams = reactive<Api.Monitor.LoginHistorySearchParams>({
  page: 1,
  pageSize: 10,
  userName: authStore.userInfo.userName
});

// 获取登录记录
async function loadLoginHistory() {
  try {
    loadingHistory.value = true;
    const { error, data } = await fetchGetLoginHistory(loginHistoryParams);

    if (!error && data) {
      loginHistoryData.value = data.records || [];
    } else {
      window.$message?.error('获取登录记录失败');
    }
  } catch (error) {
    console.error('获取登录记录失败:', error);
    window.$message?.error('获取登录记录失败');
  } finally {
    loadingHistory.value = false;
  }
}

// 获取用户头像
const userAvatar = computed(() => {
  return authStore.userInfo.nickName?.charAt(0)?.toUpperCase() || 'U';
});

// 组件挂载时加载登录记录
onMounted(() => {
  loadLoginHistory();
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-8px overflow-hidden lt-sm:overflow-auto">
    <NCard :bordered="false" class="card-wrapper sm:flex-1-hidden" content-class="flex-col">
      <NTabs v-model:value="activeTab" type="bar" animated placement="left">
        <!-- 个人信息 -->
        <NTabPane name="profile">
          <template #tab>
            <NButton size="small" quaternary>
              <template #icon>
                <icon-ic:round-person class="text-icon" />
              </template>
              个人信息
            </NButton>
          </template>

          <div class="p-6">
            <div class="mb-6 flex items-center gap-4">
              <NAvatar :size="80" round color="#18a058">
                {{ userAvatar }}
              </NAvatar>
              <div>
                <h3 class="mb-2 text-lg font-semibold">{{ authStore.userInfo.userName }}</h3>
                <p class="text-gray-500">{{ authStore.userInfo.realName || '未设置真实姓名' }}</p>
              </div>
            </div>

            <NForm ref="profileFormRef" :model="profileModel" :rules="profileRules" label-placement="left" :label-width="100">
              <NGrid :cols="2" :x-gap="16" :y-gap="16">
                <NGridItem>
                  <NFormItem label="用户名" path="userName">
                    <NInput :value="authStore.userInfo.userName" disabled />
                  </NFormItem>
                </NGridItem>
                <NGridItem>
                  <NFormItem label="昵称" path="nickName">
                    <NInput v-model:value="profileModel.nickName" placeholder="请输入昵称" />
                  </NFormItem>
                </NGridItem>
                <NGridItem>
                  <NFormItem label="真实姓名" path="realName">
                    <NInput v-model:value="profileModel.realName" placeholder="请输入真实姓名" />
                  </NFormItem>
                </NGridItem>
                <NGridItem>
                  <NFormItem label="性别" path="gender">
                    <NRadioGroup v-model:value="profileModel.gender">
                      <NRadio value="0" label="保密" />
                      <NRadio value="1" label="男" />
                      <NRadio value="2" label="女" />
                    </NRadioGroup>
                  </NFormItem>
                </NGridItem>
                <NGridItem>
                  <NFormItem label="手机号" path="phone">
                    <NInput v-model:value="profileModel.phone" placeholder="请输入手机号" />
                  </NFormItem>
                </NGridItem>
                <NGridItem>
                  <NFormItem label="邮箱" path="email">
                    <NInput v-model:value="profileModel.email" placeholder="请输入邮箱" />
                  </NFormItem>
                </NGridItem>
              </NGrid>

              <div class="mt-6">
                <NButton type="primary" @click="handleUpdateProfile">保存修改</NButton>
              </div>
            </NForm>
          </div>
        </NTabPane>

        <!-- 修改密码 -->
        <NTabPane name="password">
          <template #tab>
            <NButton size="small" quaternary>
              <template #icon>
                <icon-ic:round-lock class="text-icon" />
              </template>
              修改密码
            </NButton>
          </template>

          <div class="p-6">
            <div class="mb-6">
              <h3 class="mb-2 text-lg font-semibold">修改密码</h3>
              <p class="text-gray-500">为了您的账户安全，请定期修改密码</p>
            </div>

            <NForm ref="passwordFormRef" :model="passwordModel" :rules="passwordRules" label-placement="left" :label-width="120">
              <NFormItem label="当前密码" path="oldPassword">
                <NInput v-model:value="passwordModel.oldPassword" type="password" show-password-on="click" placeholder="请输入当前密码" />
              </NFormItem>
              <NFormItem label="新密码" path="newPassword">
                <NInput v-model:value="passwordModel.newPassword" type="password" show-password-on="click" placeholder="请输入新密码" />
              </NFormItem>
              <NFormItem label="确认新密码" path="confirmPassword">
                <NInput v-model:value="passwordModel.confirmPassword" type="password" show-password-on="click" placeholder="请再次输入新密码" />
              </NFormItem>

              <div class="mt-6">
                <NButton type="primary" @click="handleChangePassword">修改密码</NButton>
              </div>
            </NForm>
          </div>
        </NTabPane>

        <!-- 登录记录 -->
        <NTabPane name="login-history">
          <template #tab>
            <NButton size="small" quaternary>
              <template #icon>
                <icon-ic:round-history class="text-icon" />
              </template>
              登录记录
            </NButton>
          </template>

          <div class="p-6">
            <div class="mb-6">
              <h3 class="mb-2 text-lg font-semibold">登录记录</h3>
              <p class="text-gray-500">查看您的账户登录历史</p>
            </div>

            <NDataTable
              :columns="[
                { key: 'createTime', title: '登录时间', width: 200 },
                { key: 'ip', title: 'IP地址', width: 150 },
                { key: 'ipAddr', title: '登录地点', width: 200 },
                { key: 'userAgent', title: '设备信息', width: 200 },
                { key: 'status', title: '状态', width: 100 }
              ]"
              :data="loginHistoryData"
              :pagination="{ pageSize: 10 }"
              :loading="loadingHistory"
              size="small"
            />
          </div>
        </NTabPane>
      </NTabs>
    </NCard>
  </div>
</template>

<style scoped>
.card-wrapper {
  background: var(--n-color);
}
</style>
