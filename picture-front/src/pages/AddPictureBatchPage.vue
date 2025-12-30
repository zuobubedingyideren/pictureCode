<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">批量创建图片</h2>
    <!--    图片信息表单-->
    <a-form searchText="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item searchText="searchText" label="关键词">
        <a-input v-model:value="formData.searchText" placeholder="输入关键词" allow-clear />
      </a-form-item>
      <a-form-item searchText="count" label="抓取数量">
        <a-input-number
          v-model:value="formData.count"
          placeholder="输入抓取数量"
          style="min-width: 180px"
          :min="1"
          :max="30"
          allow-clear
        />
      </a-form-item>
      <a-form-item searchText="searchTextPrefix" label="名称前缀">
        <a-auto-complete
          v-model:value="formData.searchTextPrefix"
          placeholder="输入名称前缀，会自动补充序号"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading"
          >执行任务</a-button
        >
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import router from '@/router'
import {
  uploadPictureByBatchUsingPost,
} from '@/api/tupianjiekou'

const loading = ref(false)
const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})

//提交表单
const handleSubmit = async (values: any) => {
  loading.value = true
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  //   上传成功
  if (res.data.code === 0 && res.data.data) {
    message.success(`创建成功,共 ${res.data.data}条`)
    // 跳转到主页
    router.push({
      path: '/',
    })
  } else {
    message.error('创建失败' + res.data.message)
  }
  loading.value = false
}

</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
