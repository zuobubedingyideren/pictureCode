<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://www.codefather.cn" target="_blank">
          编程导航 by 程序员鱼皮
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>
<script setup lang="ts">
import GlobalHeader from '@/components/icons/GlobalHeader.vue'
</script>
<style scoped>
#basicLayout .header{
  background-color: white;
  padding-inline: 20px;
  margin-bottom: 16px;
  color: unset;
}
#basicLayout .content{
  padding: 20px;
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
}
#basicLayout .footer{
  background-color: #efefef;
  padding: 16px;
  position: fixed;
  left: 0;
  bottom: 0;
  right: 0;
  text-align: center;
}
</style>
