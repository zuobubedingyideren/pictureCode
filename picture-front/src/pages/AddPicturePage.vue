<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">{{ route.query?.id ? '修改图片' : ' 创建图片' }}</h2>
    <!--    图片上传组件-->
    <PictureUpload :picture="picture" :onSuccess="onSuccess" />
    <!--    图片信息表单-->
    <a-form
      v-if="picture"
      name="pictureForm"
      layout="vertical"
      :model="pictureForm"
      @finish="handleSubmit"
    >
      <a-form-item name="name" label="名称">
        <a-input v-model:value="pictureForm.name" placeholder="输入名称" allow-clear />
      </a-form-item>
      <a-form-item name="introduction" label="简介">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="输入简介"
          :auto-size="{ minRows: 2, maxRows: 5 }"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-auto-complete
          v-model:value="pictureForm.category"
          :options="categoryOption"
          placeholder="输入分类"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="tags" label="标签">
        <a-select
          v-model:value="pictureForm.tags"
          :mode="tags"
          :options="tagOption"
          placeholder="输入标签"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">创建</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import { userLoginUsingPost } from '@/api/userController'
import { message } from 'ant-design-vue'
import router from '@/router'
import { useRoute } from 'vue-router'

const picture = ref<pictureVO>()
const pictureForm = reactive<API.pictureEditRequest>({})

const onSuccess = (newPicture: API.pictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

//提交表单
const handleSubmit = async (values: any) => {
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    ...values,
  })
  //   上传成功
  if (res.data.code === 0 && res.data.data) {
    message.success('创建成功')
    router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error('创建失败' + res.data.message)
  }
}

const categoryOption = ref<string[]>([])
const tagOption = ref<string[]>([])

// 获取分类和标签选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  // 操作成功
  if (res.data.data === 0 && res.data.data) {
    categoryOption.value = (res.data.categorylist ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    tagOption.value = (res.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('创建失败' + res.data.message)
  }
}
onMounted(() => {
  getTagCategoryOptions()
})

// 获取老数据
const route = useRoute()
const getOldPicture = async () => {
  //   获取id
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id
    })
    if (res.data.code ===0 && res.data.data){
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
})
</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
