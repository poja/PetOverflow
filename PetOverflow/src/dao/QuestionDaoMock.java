package dao;

import java.util.ArrayList;

public class QuestionDaoMock implements QuestionDao {

	@Override
	public ArrayList<String> getNewestQuestions(int number) {
		ArrayList<String> ans = new ArrayList<>();
		ans.add("What is a good name for a pet turtle?");
		ans.add("Why does my dog eat everything?");
		return ans;
	}

}
