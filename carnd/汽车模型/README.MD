- 多项式拟合路径

  > 一般三次多项式就能满足要求

  ```c++
  // In this quiz you'll fit a polynomial to waypoints.

  #include <iostream>
  #include "Dense"

  using namespace Eigen;

  // Evaluate a polynomial.
  double polyeval(Eigen::VectorXd coeffs, double x) {
    double result = 0.0;
    for (int i = 0; i < coeffs.size(); i++) {
      result += coeffs[i] * pow(x, i);
    }
    return result;
  }

  // Fit a polynomial.
  // Adapted from
  // https://github.com/JuliaMath/Polynomials.jl/blob/master/src/Polynomials.jl#L676-L716
  Eigen::VectorXd polyfit(Eigen::VectorXd xvals, Eigen::VectorXd yvals,
                          int order) {
    assert(xvals.size() == yvals.size());
    assert(order >= 1 && order <= xvals.size() - 1);
    Eigen::MatrixXd A(xvals.size(), order + 1);

    for (int i = 0; i < xvals.size(); i++) {
      A(i, 0) = 1.0;
    }

    for (int j = 0; j < xvals.size(); j++) {
      for (int i = 0; i < order; i++) {
        A(j, i + 1) = A(j, i) * xvals(j);
      }
    }

    auto Q = A.householderQr();
    auto result = Q.solve(yvals);
    return result;
  }

  int main() {
    Eigen::VectorXd xvals(6);
    Eigen::VectorXd yvals(6);
    // x waypoint coordinates
    xvals << 9.261977, -2.06803, -19.6663, -36.868, -51.6263, -66.3482;
    // y waypoint coordinates
    yvals << 5.17, -2.25, -15.306, -29.46, -42.85, -57.6116;

    // TODO: use `polyfit` to fit a third order polynomial to the (x, y)
    // coordinates.
    
    Eigen::VectorXd fit_curve = polyfit(xvals,yvals,3);

    for (double x = 0; x <= 20; x += 1.0) {
      // TODO: use `polyeval` to evaluate the x values.
      std::cout << polyeval(fit_curve,x) << std::endl; 
    }

    // Expected output
    // -0.905562
    // -0.226606
    // 0.447594
    // 1.11706
    // 1.7818
    // 2.44185
    // 3.09723
    // 3.74794
    // 4.39402
    // 5.03548
    // 5.67235
    // 6.30463
    // 6.93236
    // 7.55555
    // 8.17423
    // 8.7884
    // 9.3981
    // 10.0033
    // 10.6041
    // 11.2005
    // 11.7925
  }
  ```

- 总计

  ## What we have learned so far

  From the previous lessons we have learned to apply the bicycle model, polynomial fitting, low complexity heuristics (e.g. CTE), and short time steps, to enable vehicles to follow a complex (polynomial) trajectories. This is an effective, practical, and commonly used approach, which can be applied to many autonomous vehicle scenarios, in real time.

  ## Coming Next

  For the next few lessons we will round out our discussion of vehicle models with an overview of the more comprehensive, but less practical, dynamic models. Dynamic models and the elements which comprise them are rigorous and could be modules or courses unto themselves. The content that follows is targeted developing awareness and intuition that can be applied to further study and consists of:

  - Dynamic Model Forces
  - Tire Slip Angle
  - Tire Slip Ratio
  - Tire Models

  Additional resources are linked to each lesson to encourage and enable more in depth study. [One of these resources](http://www.me.berkeley.edu/~frborrel/pdfpub/IV_KinematicMPC_jason.pdf) makes a good case for the use of lower complexity kinematic models, as:

  > Compared to higher fidelity vehicle models, the system identification on the kinematic bicycle model is easier because there are only two parameters to identify, lf and lr. This makes it simpler to port the same controller or path planner to other vehicles with differently sized wheelbases.

  To further expand on this, lower complexity models have two strong advantages over higher complexity (dynamic included) models. They can run in real time (essential requirement of any model operating in a vehicle) and they are transferable to vehicles with differing attributes, such as mass. To use a dynamic model engineers would have to be able to control the vehicle attributes of the vehicles they are deploying models into (they probably won't have control over this). High complexity models would need major re-adjustment to account for even small differences. Lower complexity models do not suffer from this constraint and so can be placed in a wider range of vehicles, with far less additional effort, and unpredictability.

  Another frequently asked question is where our model comes from and why it differs from other models seen in the program and from other sources.

  The kinematic model we derive and use here is not quite the same as in the Berkeley paper (linked above), although they are similar. It is possible to use different models in different parts of your pipeline, depending on what levels of accuracy you need in different places. It is common to see this in industry. The principles of model we present can be applied to add parameters into the model to make models fit to purpose.

- 运动模型—力

  > 汽车所受力可以总结为横向力和纵向力度

  ![](imgs/1.jpg)

- 运动模型—偏离角

  > 偏离角可以帮助测量转弯

  ![](imgs/2.jpg)

- 驱动器约束条件

  ​