# ProjectLocalSearch
Trong thư mục LocalSearchUsingCBLS có package BTL_Productplaning chứa code của bài tập lớn:
 _ HillClimbingConstraintThenFunctionNeighborhoodExplorer.java cài đặt thuật toán hillclimbingsearch có thêm hàm mục tiêu theo kiểu ưu tiên tối ưu ràng buộc trước sau đó mới tối ưu mục tiêu
 _TabuSearchCustom.java cài đặt thuật toán tabusearch với mục tiêu và ràng buộc được tối ưu theo dạng đặt hệ số cho cả mục tiêu và ràng buộc sau đó tối ưu đồng thời 2 mục tiêu này.
 _TabuNoHope.java cài đặt thuật toán tabusearch với hàm move customize và thay đổi các hàm tính violation cho phù hợp.
 _productionPlanning.java mô hình hóa bài toán đầu vào, và gọi các hàm search, cũng như khởi tạo 1 lời giải ban đầu cho bài toán bằng phương pháp greedy.
 _data nằm trong thư mục ./data/productplaning
 Thư mục Model_Mip chứa code mô hình mip python.
 Thư mục GA chứa code GA và các test.
 