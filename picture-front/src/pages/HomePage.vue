<template>
  <div id="homePage">
    <!--    搜索框-->
    <div class="search-bar">
      <a-input-search
        v-model:value="searchParams.searchText"
        placeholder="在海量图片中搜索"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />
    </div>
    <!--    分类和标签搜索-->
    <a-tabs v-model:activeKey="selectCategory" @change="doSearch">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane v-for="category in categoryList" key="category" tab="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">标签：</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagsList"
          :key="tag"
          v-model:checked="selectTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>
    <!--    图片列表-->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="datalist"
      :pagination="pagination"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!--          单张图片-->
          <a-card hoverable @click="doClickPicture(picture)">
            <template #cover>
              <img alt="picture.name" :src="picture.url" style="height: 180px; object-fit: cover" />
            </template>
            <a-card-meta :title="picture.name">
              <template #description>
                <a-flex>
                  <a-tag color="green">{{ picture.category ?? '默认分类' }}</a-tag>
                  <a-tag v-for="tag in picture.tags || []" :key="tag">
                    {{ tag }}
                  </a-tag>
                </a-flex>
              </template>
            </a-card-meta>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { listPictureTagCategoryUsingGet, listPictureVoByPageUsingPost } from '@/api/tupianjiekou'

// 定义数据
const datalist = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = ref<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 分页功能
const pagination = computed(() => {
  return {
    current: searchParams.value.current ?? 1,
    pageSize: searchParams.value.pageSize ?? 10,
    total: total.value,
    onChange: (page, pageSize) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    ...searchParams,
    tag: [],
  }
  selectTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tag.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPageUsingPost({
    ...searchParams.value,
  })
  if (res.data.code === 0 && res.data.data) {
    datalist.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
  loading.value = false
}
const router = useRouter()
// 点击图片跳转到图片详情页面
const doClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}
// 页面加载时获取数据，只请求一次
onMounted(() => {
  fetchData()
})

// 搜索功能
const doSearch = async () => {
  // 搜索之后重置页码
  searchParams.current = 1
  fetchData()
}

//通过标签和分类搜索
const categoryList = ref<string[]>([])
const selectCategory = ref<string>('all')
const tagList = ref<string[]>([])
const selectTagList = ref<string[]>([])

// 获取分类和标签选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    categoryList.value = res.data.categorylist ?? []
    tagList.value = res.data.tagList ?? []
  } else {
    message.error('加载分类标签失败' + res.data.message)
  }
}
onMounted(() => {
  getTagCategoryOptions()
})
</script>

<style scoped>
#homePage {
  margin-bottom: 16px;
}
#homePage .search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}
#homePage .tag-bar {
  margin-bottom: 16px;
}
</style>
