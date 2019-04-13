package com.kvlt.view;

import com.kvlt.domain.ResultModel;
import com.kvlt.domain.ResultViewModel;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * TestController
 *
 * @author KVLT
 * @date 2019-04-13.
 */
public class TestController {

    //*************mock data**************//
    private static List<ResultModel> resultModelList = new ArrayList<>();

    static {
        ResultModel model = new ResultModel();
        model.setId(1);
        model.setContent("This is first model");
        resultModelList.add(model);

        model = new ResultModel();
        model.setId(2);
        model.setContent("This is second model");
        resultModelList.add(model);
    }

    public Mono<ServerResponse> listView(ServerRequest serverRequest) {
        ResultModel rm = new ResultModel();
        ResultViewModel model = new ResultViewModel(200, "success", rm);
        List<ResultViewModel> resultViewModels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            resultViewModels.add(model);
        }

        Flux<ResultViewModel> modelFlux = Flux.fromIterable(resultViewModels);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(modelFlux, ResultViewModel.class);
    }

    public Mono<ResultViewModel> extraResult(ServerRequest serverRequest) {
        int id = Integer.parseInt(serverRequest.pathVariable("id"));
        ResultModel model = null;
        ResultViewModel resultViewModel;

        for (ResultModel m : resultModelList) {
            if (m.getId() == id) {
                model = m;
                break;
            }
        }

        if (model != null) {
            resultViewModel = new ResultViewModel(200, "ok", model);
        } else {
            resultViewModel = ResultViewModel.EMPTY_RESULT;
        }

        //return the result.
        return Mono.just(resultViewModel);
    }

    public Mono<ServerResponse> flowAllResult(ServerRequest serverRequest) {
        List<ResultViewModel> result = new ArrayList<>();
        for (ResultModel model : resultModelList) {
            result.add(new ResultViewModel(200, "ok", model));
        }

        return ServerResponse.ok().body(Flux.fromIterable(result), ResultViewModel.class);
    }

    public Mono<ResultViewModel> putItem(ServerRequest serverRequest) {

        //get the object and put to list
        Mono<ResultModel> model = serverRequest.bodyToMono(ResultModel.class);
        final ResultModel[] data = new ResultModel[1];

        model.doOnNext(new Consumer<ResultModel>() {
            @Override
            public void accept(ResultModel model) {

                //check if we can put this data
                boolean check = true;
                for (ResultModel r : resultModelList) {
                    if (r.getId() == model.getId()) {
                        check= false;
                        break;
                    }
                }

                if (check) {
                    data[0] = model;
                    //put it!
                    resultModelList.add(model);
                } else {
                    data[0] = null; //error
                }
            }
        }).thenEmpty(Mono.empty());

        ResultViewModel resultViewModel;
        if (data[0] == null) { //error
            resultViewModel = new ResultViewModel(200, "ok", data[0]);
        } else { //success
            resultViewModel = ResultViewModel.EMPTY_RESULT;
        }

        //return the result
        return Mono.just(resultViewModel);
    }

}
