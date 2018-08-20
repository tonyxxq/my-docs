- outline

  ![](./imgs/1.png)

- overview

  ![](./imgs/2.png)

- 有限状态机（Finite State Machines）

  > 不能转换成其他状态点作为Accepting State.

  ![](./imgs/3.png)

- 有限状态机的优缺点

  > 图中使用了自动售货机作为例子

  ![](./imgs/4.png)

- 高速路上使用有限状态机的例子

  ![](./imgs/5.png)

  > 输入数据，除了之前的状态，其他都需要。

  ![](./imgs/6.png)

- 转换函数，伪代码

  ```python
  def transition_function(predictions, current_fsm_state, current_pose, cost_functions, weights):
      # only consider states which can be reached from current FSM state.
      possible_successor_states = successor_states(current_fsm_state)

      # keep track of the total cost of each state.
      costs = []
      for state in possible_successor_states:
          # generate a rough idea of what trajectory we would
          # follow IF we chose this state.
          trajectory_for_state = generate_trajectory(state, current_pose, predictions)

          # calculate the "cost" associated with that trajectory.
          cost_for_state = 0
          for i in range(len(cost_functions)) :
              # apply each cost function to the generated trajectory
              cost_function = cost_functions[i]
              cost_for_cost_function = cost_function(trajectory_for_state, predictions)

              # multiply the cost by the associated weight
              weight = weights[i]
              cost_for_state += weight * cost_for_cost_function
           costs.append({'state' : state, 'cost' : cost_for_state})

      # Find the minimum cost state.
      best_next_state = None
      min_cost = 9999999
      for i in range(len(possible_successor_states)):
          state = possible_successor_states[i]
          cost  = costs[i]
          if cost < min_cost:
              min_cost = cost
              best_next_state = state 

      return best_next_state
  ```

- SPEED COST

  ![](./imgs/7.png)

  Example Cost Function - Lane Change Penalty

  ![](imgs/8.png)

  In the image above, the blue self driving car (bottom left) is trying to get to the goal (gold star). It's currently in the correct lane but the green car is going very slowly, so it considers whether it should perform a lane change (LC) or just keep lane (KL). These options are shown as lighter blue vehicles with a dashed outline.

  If we want to design a cost function that deals with lane choice, it will be helpful to establish what the relevant variables are. In this case, we can define:

  - **\Delta s = s_G - sΔs=sG​−s** how much distance the vehicle will have before it has to get into the goal lane.
  - **\Delta d = d_G - d_{LC/KL}Δd=dG​−dLC/KL​** the lateral distance between the goal lane and the options being considered. In this case \Delta d_{KL} = d_G - d_{KL}ΔdKL​=dG​−dKL​ would be zero and \Delta d_{LC} = d_G - d_{LC}ΔdLC​=dG​−dLC​ would not.