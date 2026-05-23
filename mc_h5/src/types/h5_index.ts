// 首页轮播图数据类型
export type HomeSlides ={ 
    id: number;
    stype: string;
    stype_link: string;
    title: string;
    stype_text: string;
    pic_image_url: string;
}

// 轮播图数据类型
export type HomeNav2s =HomeSlides &{
    cat_text: string;
    tcolor:string;
}

// 首页医院数据类型
export type HomeHospitals ={
    id: number;
    name: string;
    rank: string;
    label: string;
    intro: string;
    avatar_url: string;
}

// 首页数据类型
export type HomeIndex ={
    now: string;
    slides: HomeSlides[];
    nav2s: HomeNav2s[];
    navs: HomeNav2s[];
    hospitals: HomeHospitals[];
}


