- 加快brew的下载速度，替换下载源

  **1. 替换formula 索引的镜像（即 brew update 时所更新内容）**

  ```
  cd "$(brew --repo)"
  git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git

  cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
  git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-core.git

  brew update

  ```

  **2. 替换Homebrew 二进制预编译包的镜像**

  ```
  echo 'export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.tuna.tsinghua.edu.cn/homebrew-bottles' >> ~/.bash_profile
  source ~/.bash_profile
  ```