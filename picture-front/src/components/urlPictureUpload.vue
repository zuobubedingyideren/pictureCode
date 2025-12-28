<template>
  <div class="url-picture-upload">
    <a-input-group compact>
      <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="请输入图片地址" />
      <a-button type="primary" style="width: 120px" :loading="loading" @click="handleUpload">提交</a-button>
    </a-input-group>
    <div class="img-wrapper">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar">
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { UploadChangeParam, UploadProps } from 'ant-design-vue';
import { testUploadFileUsingPost } from '@/api/fileController'
import { uploadPictureUsingPost } from '@/api/tupianjiekou'

interface Props{
  picture?: API.PictureVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()
const fileUrl= ref<string>()
const loading = ref<boolean>(false);

// 上传图片功能
const handleUpload =async () => {
  loading.value = true;
  try {
    const params : API.uploadPictureUsingPOSTParams = {fileUrl:fileUrl.value}
    if (props.picture){
      params.id = props.picture.id
    }
    const res = await uploadPictureUsingPost(params)
    if(res.data.code === 0 && res.data.data){
      message.success('上传成功！');
    //   将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
    }else{
      message.error('上传失败！' + res.data.message);
    }
  }catch(error){
    console.error('图片上传失败',error)
    message.error('图片上传失败')
    }
  loading.value = false;
};
</script>
<style scoped>
.picture-upload :deep(.ant-upload){
  width: 100% !important;
  height: 100% !important;
  min-width: 152px;
  min-height: 152px;
}
.picture-upload img{
  max-width: 100%;
  max-height:480px
}
.avatar-uploader > .ant-upload {
  width: 128px;
  height: 128px;
}
.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>
